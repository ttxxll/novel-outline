package com.noveloutline.analyzer.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.config.AiProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiClientConfig {

    /**
     * 配置了ai.provider=deepseek就初始化DeepSeekClient
     */
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "deepseek")
    public AiClient deepSeekClient(AiProperties properties, ObjectMapper objectMapper) {
        return new DeepSeekClient(properties, objectMapper);
    }

    /**
     * 配置了ai.provider=mimo就初始化miMoClient
     */
    @Bean
    @ConditionalOnProperty(name = "ai.provider", havingValue = "mimo")
    public AiClient miMoClient(AiProperties properties, ObjectMapper objectMapper) {
        return new MiMoClient(properties, objectMapper);
    }
}
