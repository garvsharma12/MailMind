package com.draig.mailmind_sb;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.net.URI;
import java.util.List;
import java.util.Map;

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
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                )
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
        return extractResponseContent(response);
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

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an assistant that writes email replies.\n");
        prompt.append("⚠️ Important rules:\n");
        prompt.append("- Do NOT include any subject line.\n");
        prompt.append("- Only generate the reply body starting with a greeting (e.g., Dear ...).\n");
        prompt.append("- Keep the reply professional and natural.\n");
        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");
        }
        prompt.append("\nThe email content is:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}