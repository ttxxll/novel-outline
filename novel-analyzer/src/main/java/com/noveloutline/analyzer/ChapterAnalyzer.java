package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.client.DeepSeekClient;
import com.noveloutline.analyzer.prompt.ChapterPrompt;
import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import org.springframework.stereotype.Component;

@Component
public class ChapterAnalyzer {

    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;

    public ChapterAnalyzer(DeepSeekClient deepSeekClient, ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
    }

    public ChapterAnalysisResult analyze(String chapterTitle, String chapterContent, NovelContext context) {
        String systemPrompt = ChapterPrompt.systemPrompt();
        String userMessage = ChapterPrompt.userMessage(chapterTitle, chapterContent, context);

        String rawJson = deepSeekClient.chat(systemPrompt, userMessage);
        rawJson = extractJson(rawJson);

        try {
            return objectMapper.readValue(rawJson, ChapterAnalysisResult.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse chapter analysis result", e);
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
