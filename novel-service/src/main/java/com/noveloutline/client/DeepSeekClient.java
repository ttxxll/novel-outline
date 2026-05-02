package com.noveloutline.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.noveloutline.config.AiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class DeepSeekClient implements AiClient {

    private final RestTemplate restTemplate;
    private final AiProperties properties;
    private final ObjectMapper objectMapper;

    public DeepSeekClient(AiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30_000);
        factory.setReadTimeout(600_000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public String chat(String systemPrompt, String userMessage, int maxTokens) {
        AiProperties.DeepSeekConfig config = properties.getDeepseek();
        log.debug("Calling DeepSeek API: model={}, userMessageLength={}", config.getModel(), userMessage.length());

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", config.getModel());
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("temperature", config.getTemperature());

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
        headers.setBearerAuth(config.getApiKey());

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        long startTime = System.currentTimeMillis();
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                config.getBaseUrl() + config.getChatPath(),
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
