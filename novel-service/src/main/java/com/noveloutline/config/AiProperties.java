package com.noveloutline.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private String provider = "deepseek";

    private DeepSeekConfig deepseek = new DeepSeekConfig();
    private MiMoConfig mimo = new MiMoConfig();

    @Data
    public static class DeepSeekConfig {
        private String apiKey;
        private String baseUrl = "https://api.deepseek.com";
        private String chatPath = "/v1/chat/completions";
        private String model = "deepseek-v4-pro";
        private int maxTokens = 8192;
        private double temperature = 0.3;
    }

    @Data
    public static class MiMoConfig {
        private String apiKey;
        private String baseUrl = "https://api.xiaomimimo.com";
        private String chatPath = "/anthropic/v1/messages";
        private String model = "mimo-v2.5-pro";
        private int maxTokens = 8192;
        private double temperature = 0.3;
    }
}
