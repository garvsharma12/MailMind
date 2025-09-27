package com.draig.mailmind_sb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReplyGeneratorService {

    private final WebClient webClient;

    @Value("${api.url}")
    private String apiUrl; // Full URL, e.g., https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent

    @Value("${api.key}")
    private String apiKey;

    public ReplyGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateReply(EmailRequest emailRequest) {
        String prompt = buildPrompt(emailRequest);

        int maxOutputTokens = pickMaxTokens(emailRequest.getLength());
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("maxOutputTokens", maxOutputTokens);
        generationConfig.put("temperature", 0.7);
        generationConfig.put("topP", 0.95);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", generationConfig
        );

        String response;
        try {
            response = webClient.post()
                    .uri(URI.create(apiUrl + "?key=" + apiKey))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException ex) {
            String body = ex.getResponseBodyAsString();
            return "Upstream API error (" + ex.getStatusCode() + "): " + (body != null ? body : ex.getMessage());
        } catch (Exception ex) {
            return "Failed to call upstream API: " + ex.getMessage();
        }
        String raw = extractResponseContent(response);
        return sanitizeModelOutput(raw, emailRequest.getEmailContent());
    }

    private int pickMaxTokens(String length) {
        if (length == null) return 800; // default medium-long
        String l = length.trim().toLowerCase();
        return switch (l) {
            case "short" -> 400;
            case "long" -> 1200;
            default -> 800; // medium or unknown
        };
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidates = rootNode.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    String text = parts.get(0).path("text").asText(null);
                    if (text != null) {
                        return text;
                    }
                }
            }
            return "Unable to generate reply. Unexpected response format.";
        } catch (Exception e) {
            return "Unable to generate reply. Please try again later. " + e.getMessage();
        }
    }

    // Remove any echoed original content and common quote patterns from the model output.
    private String sanitizeModelOutput(String output, String originalEmail) {
        if (output == null || output.isBlank()) return output;
        String cleaned = output;

        // Remove our explicit delimiters block if the model copied it back
        cleaned = cleaned.replaceAll("(?s)-----8<----- BEGIN ORIGINAL EMAIL -----.*?-----8<----- END ORIGINAL EMAIL -----\\n?", "");

        // Strip common email header/quote indicators if the model reproduced them
        cleaned = cleaned.replaceAll("(?im)^>+.*$", ""); // quoted lines
        cleaned = cleaned.replaceAll("(?im)^On .*wrote:.*$", "");
        cleaned = cleaned.replaceAll("(?im)^From:.*$", "");
        cleaned = cleaned.replaceAll("(?im)^Sent:.*$", "");
        cleaned = cleaned.replaceAll("(?im)^Subject:.*$", "");
        cleaned = cleaned.replaceAll("(?im)^To:.*$", "");
        cleaned = cleaned.replaceAll("(?im)^Cc:.*$", "");
        cleaned = cleaned.replaceAll("(?im)^-----+\n?Original Message\n?-----+", "");

        // Remove exact original email content if present verbatim
        if (originalEmail != null && !originalEmail.isBlank()) {
            cleaned = cleaned.replace(originalEmail, "");
        }

        // Prepare normalized original for substring checks
        String normalizedOriginal = normalizeForComparison(originalEmail);

        // Build a set of meaningful lines from the original to filter exact line echoes
        Set<String> originalLines = new HashSet<>();
        if (originalEmail != null) {
            for (String line : originalEmail.split("\\r?\\n")) {
                String t = line.strip();
                if (t.length() >= 8) { // only filter reasonably long lines to avoid over-removal
                    originalLines.add(t);
                }
            }
        }

        // Remove quoted lines and any lines that exactly match significant original lines
        List<String> kept = cleaned.lines()
                .map(String::strip)
                .filter(l -> !l.isEmpty())
                .filter(l -> !originalLines.contains(l))
                .filter(l -> !isMostlyFromOriginal(l, normalizedOriginal))
                .collect(Collectors.toList());

        cleaned = String.join("\n", kept).trim();

        // If we removed everything by accident, fall back to original output
        if (cleaned.isBlank()) return output.trim();
        return cleaned;
    }

    // Heuristic: consider a line "mostly from original" if the normalized line is a substring of the normalized original,
    // or if more than 70% of its words appear in the original.
    private boolean isMostlyFromOriginal(String line, String normalizedOriginal) {
        if (normalizedOriginal == null || normalizedOriginal.isBlank()) return false;
        String nLine = normalizeForComparison(line);
        if (nLine.isBlank()) return false;
        if (normalizedOriginal.contains(nLine)) return true;

        String[] words = nLine.split("\\s+");
        if (words.length == 0) return false;
        int present = 0;
        for (String w : words) {
            if (w.length() < 3) continue; // ignore very short words
            if (normalizedOriginal.contains(w)) present++;
        }
        double ratio = present / (double) Math.max(1, words.length);
        return ratio >= 0.7;
    }

    private String normalizeForComparison(String s) {
        if (s == null) return null;
        return s.toLowerCase(Locale.ROOT)
                .replaceAll("[\\p{Punct}]", " ") // remove punctuation
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String buildPrompt(EmailRequest emailRequest) {
        String tone = (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty())
                ? emailRequest.getTone()
                : "polite and professional";

        String lengthHint;
        String length = emailRequest.getLength() == null ? "medium" : emailRequest.getLength().trim().toLowerCase();
        switch (length) {
            case "short" -> lengthHint = "Write 4-6 concise sentences.";
            case "long" -> lengthHint = "Write 10-14 well-structured sentences across 2-3 paragraphs.";
            default -> lengthHint = "Write 6-9 sentences across 1-2 paragraphs.";
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert email assistant.\n");
        prompt.append("Task: Draft a helpful email reply to the message below.\n");
        prompt.append("Rules (must follow):\n");
        prompt.append("- Do NOT include any subject line.\n");
        prompt.append("- Start with a natural greeting (e.g., Dear/Hi [Name if known]).\n");
        prompt.append("- Use a ").append(tone).append(" tone.\n");
        prompt.append("- ").append(lengthHint).append("\n");
        prompt.append("- Do NOT quote, restate, or copy the original email text. Summarize only what is necessary to respond.\n");
        prompt.append("- The reply must be entirely new wording (no blocks of the original text).\n");
        prompt.append("- Keep it clear and action-oriented; include next steps or questions if needed.\n");
        prompt.append("- End with an appropriate sign-off.\n\n");
        prompt.append("Original email (for context only — DO NOT echo):\n");
        prompt.append("-----8<----- BEGIN ORIGINAL EMAIL -----\n");
        prompt.append(emailRequest.getEmailContent()).append("\n");
        prompt.append("-----8<----- END ORIGINAL EMAIL -----\n");
        return prompt.toString();
    }
}

