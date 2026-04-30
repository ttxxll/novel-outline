package com.noveloutline.analyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.client.DeepSeekClient;
import com.noveloutline.analyzer.prompt.VolumePrompt;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VolumeAggregator {

    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;

    public VolumeAggregator(DeepSeekClient deepSeekClient, ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
    }

    public VolumeAnalysisResult aggregate(String volumeTitle, List<String> chapterAnalysisJsons) {
        String chaptersJson;
        try {
            chaptersJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chapterAnalysisJsons);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize chapter analyses", e);
        }

        String rawJson = deepSeekClient.chat(
                VolumePrompt.systemPrompt(),
                VolumePrompt.userMessage(volumeTitle, chaptersJson));

        rawJson = extractJson(rawJson);
        try {
            return objectMapper.readValue(rawJson, VolumeAnalysisResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse volume analysis result", e);
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
