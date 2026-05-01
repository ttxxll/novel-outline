package com.noveloutline.analyzer.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noveloutline.analyzer.config.AiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class MiMoClient implements AiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AiProperties properties;
    private final ObjectMapper objectMapper;

    public MiMoClient(AiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        AiProperties.MiMoConfig config = properties.getMimo();
        log.debug("Calling MiMo API: model={}, userMessageLength={}", config.getModel(), userMessage.length());

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", config.getMaxTokens());
        requestBody.put("temperature", config.getTemperature());
        requestBody.put("system", systemPrompt);

        ArrayNode messages = objectMapper.createArrayNode();
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        requestBody.set("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", config.getApiKey());

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        long startTime = System.currentTimeMillis();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                config.getBaseUrl() + config.getChatPath(),
                HttpMethod.POST,
                entity,
                String.class);
        long elapsed = System.currentTimeMillis() - startTime;

        String response = responseEntity.getBody();
        log.debug("MiMo response: elapsed={}ms, responseLength={}", elapsed, response != null ? response.length() : 0);

        try {
            JsonNode root = objectMapper.readTree(response);
            String content = root.path("content").get(0).path("text").asText();
            log.info("MiMo API call successful: elapsed={}ms, contentLength={}", elapsed, content.length());
            return content;
        } catch (Exception e) {
            log.error("Failed to parse MiMo response: response={}", response, e);
            throw new RuntimeException("Failed to parse MiMo response: " + response, e);
        }
    }
}
