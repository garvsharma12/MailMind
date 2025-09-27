package com.draig.mailmind_sb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        if (length == null) return 512; // default medium-long
        String l = length.trim().toLowerCase();
        return switch (l) {
            case "short" -> 200;
            case "long" -> 800;
            default -> 512; // medium or unknown
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

        // Remove exact original email content if present verbatim
        if (originalEmail != null && !originalEmail.isBlank()) {
            cleaned = cleaned.replace(originalEmail, "");
        }

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
                .map(String::stripTrailing)
                .filter(l -> !l.stripLeading().startsWith(">"))
                .filter(l -> !originalLines.contains(l.strip()))
                .collect(Collectors.toList());

        cleaned = kept.stream().dropWhile(String::isBlank).collect(Collectors.joining("\n")).trim();

        // If we removed everything by accident, fall back to original output
        if (cleaned.isBlank()) return output.trim();
        return cleaned;
    }

    private String buildPrompt(EmailRequest emailRequest) {
        String tone = (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty())
                ? emailRequest.getTone()
                : "polite and professional";

        String lengthHint = switch (emailRequest.getLength() == null ? "medium" : emailRequest.getLength().trim().toLowerCase()) {
            case "short" -> "Keep it brief (3-5 sentences).";
            case "long" -> "Provide a detailed reply (8-12 sentences), with clear structure and next steps.";
            default -> "Aim for a medium length (5-8 sentences).";
        };

        StringBuilder prompt = new StringBuilder();
        prompt.append("Task: Write a concise email reply to the message below.\n");
        prompt.append("Requirements:\n");
        prompt.append("- Do NOT include any subject line.\n");
        prompt.append("- Start with a natural greeting (e.g., Dear/Hi [Name if known]).\n");
        prompt.append("- Use a ").append(tone).append(" tone.\n");
        prompt.append("- ").append(lengthHint).append("\n");
        prompt.append("- Do NOT quote, restate, or copy the original email text. Paraphrase only what is necessary to respond.\n");
        prompt.append("- Keep it clear, helpful, and human; include next steps or questions if needed.\n");
        prompt.append("- End with an appropriate sign-off.\n\n");
        prompt.append("Original email (for context only, DO NOT echo):\n");
        prompt.append("-----8<----- BEGIN ORIGINAL EMAIL -----\n");
        prompt.append(emailRequest.getEmailContent()).append("\n");
        prompt.append("-----8<----- END ORIGINAL EMAIL -----\n");
        return prompt.toString();
    }
}