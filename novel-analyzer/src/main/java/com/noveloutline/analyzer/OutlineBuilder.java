package com.noveloutline.analyzer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.client.AiClient;
import com.noveloutline.analyzer.prompt.OutlinePrompt;
import com.noveloutline.common.dto.OutlineResult;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class OutlineBuilder {

    @Autowired
    private AiClient aiClient;
    @Autowired
    private ObjectMapper objectMapper;
    public OutlineResult build(String novelTitle, List<VolumeAnalysisResult> volumeResults) {
        log.info("Building outline: novelTitle={}, volumeCount={}", novelTitle, volumeResults.size());
        String volumesJson;
        try {
            volumesJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(volumeResults);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize volume results: novelTitle={}", novelTitle, e);
            throw new RuntimeException("Failed to serialize volume results", e);
        }
        String rawJson = aiClient.chat(
                OutlinePrompt.systemPrompt(),
                OutlinePrompt.userMessage(novelTitle, volumesJson));
        rawJson = extractJson(rawJson);
        try {
            OutlineResult result = objectMapper.readValue(rawJson, OutlineResult.class);
            log.info("Outline build complete: title={}, characterIndexSize={}, locationTimelineSize={}",
                    novelTitle,
                    result.characterIndex != null ? result.characterIndex.size() : 0,
                    result.locationTimeline != null ? result.locationTimeline.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("Failed to parse outline result: novelTitle={}", novelTitle, e);
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
