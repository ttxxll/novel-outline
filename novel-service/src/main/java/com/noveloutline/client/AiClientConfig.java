package com.noveloutline.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.config.AiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiClientConfig {

    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "deepseek")
    public AiClient deepSeekClient(AiProperties properties, ObjectMapper objectMapper) {
        return new DeepSeekClient(properties, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "mimo")
    public AiClient miMoClient(AiProperties properties, ObjectMapper objectMapper) {
        return new MiMoClient(properties, objectMapper);
    }
}
