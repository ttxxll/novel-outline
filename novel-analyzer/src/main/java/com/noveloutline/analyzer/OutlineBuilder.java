package com.noveloutline.analyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.client.DeepSeekClient;
import com.noveloutline.analyzer.prompt.OutlinePrompt;
import com.noveloutline.common.dto.OutlineResult;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutlineBuilder {

    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;

    public OutlineBuilder(DeepSeekClient deepSeekClient, ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
    }

    public OutlineResult build(String novelTitle, List<VolumeAnalysisResult> volumeResults) {
        String volumesJson;
        try {
            volumesJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(volumeResults);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize volume results", e);
        }

        String rawJson = deepSeekClient.chat(
                OutlinePrompt.systemPrompt(),
                OutlinePrompt.userMessage(novelTitle, volumesJson));

        rawJson = extractJson(rawJson);
        try {
            return objectMapper.readValue(rawJson, OutlineResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse outline result", e);
        }
    }

    private String extractJson(String raw) {
        String trimmed = raw.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        return trimmed;
    }
}
