package com.noveloutline.analyzer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noveloutline.analyzer.config.DeepSeekProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class DeepSeekClient {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    private final RestTemplate restTemplate;
    private final DeepSeekProperties properties;
    private final ObjectMapper objectMapper;

    public DeepSeekClient(DeepSeekProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public String chat(String systemPrompt, String userMessage) {
        log.debug("Calling DeepSeek API: model={}, userMessageLength={}", properties.getModel(), userMessage.length());

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", properties.getModel());
        requestBody.put("max_tokens", properties.getMaxTokens());
        requestBody.put("temperature", properties.getTemperature());

        ArrayNode messages = objectMapper.createArrayNode();

        ObjectNode systemMsg = objectMapper.createObjectNode();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        requestBody.set("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        long startTime = System.currentTimeMillis();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                properties.getBaseUrl() + "/v1/chat/completions",
                HttpMethod.POST,
                entity,
                String.class);
        long elapsed = System.currentTimeMillis() - startTime;

        String response = responseEntity.getBody();
        log.debug("DeepSeek response: elapsed={}ms, responseLength={}", elapsed, response != null ? response.length() : 0);

        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("choices").get(0).path("message").path("content").asText();
            log.info("DeepSeek API call successful: elapsed={}ms, contentLength={}", elapsed, content.length());
            return content;
        } catch (Exception e) {
            log.error("Failed to parse DeepSeek response: response={}", response, e);
            throw new RuntimeException("Failed to parse DeepSeek response: " + response, e);
        }
    }
}
