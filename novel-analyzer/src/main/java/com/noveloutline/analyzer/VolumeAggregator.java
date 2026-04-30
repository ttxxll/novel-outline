package com.noveloutline.analyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.client.DeepSeekClient;
import com.noveloutline.analyzer.prompt.VolumePrompt;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VolumeAggregator {

    private static final Logger log = LoggerFactory.getLogger(VolumeAggregator.class);

    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;

    public VolumeAggregator(DeepSeekClient deepSeekClient, ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
    }

    public VolumeAnalysisResult aggregate(String volumeTitle, List<String> chapterAnalysisJsons) {
        log.info("Aggregating volume: title={}, chapterCount={}", volumeTitle, chapterAnalysisJsons.size());

        String chaptersJson;
        try {
            chaptersJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chapterAnalysisJsons);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize chapter analyses: volumeTitle={}", volumeTitle, e);
            throw new RuntimeException("Failed to serialize chapter analyses", e);
        }

        String rawJson = deepSeekClient.chat(
                VolumePrompt.systemPrompt(),
                VolumePrompt.userMessage(volumeTitle, chaptersJson));

        rawJson = extractJson(rawJson);
        try {
            VolumeAnalysisResult result = objectMapper.readValue(rawJson, VolumeAnalysisResult.class);
            log.info("Volume aggregation complete: title={}, keyEvents={}",
                    volumeTitle, result.keyEvents != null ? result.keyEvents.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("Failed to parse volume analysis result: title={}", volumeTitle, e);
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
