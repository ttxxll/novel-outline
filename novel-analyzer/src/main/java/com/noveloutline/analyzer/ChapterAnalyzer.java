package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.client.AiClient;
import com.noveloutline.analyzer.prompt.ChapterPrompt;
import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChapterAnalyzer {

    @Autowired
    private AiClient aiClient;
    @Autowired
    private ObjectMapper objectMapper;
    public ChapterAnalysisResult analyze(String chapterTitle, String chapterContent, NovelContext context) {
        log.info("Analyzing chapter: title={}, contentLength={}, totalChaptersAnalyzed={}",
                chapterTitle, chapterContent.length(), context.totalChaptersAnalyzed);
        String systemPrompt = ChapterPrompt.systemPrompt();
        String userMessage = ChapterPrompt.userMessage(chapterTitle, chapterContent, context);
        String rawJson = aiClient.chat(systemPrompt, userMessage);
        rawJson = extractJson(rawJson);
        try {
            ChapterAnalysisResult result = objectMapper.readValue(rawJson, ChapterAnalysisResult.class);
            log.debug("Chapter analysis parsed successfully: title={}, characters={}, factions={}",
                    chapterTitle,
                    result.characters != null ? result.characters.size() : 0,
                    result.factions != null ? result.factions.size() : 0);
            return result;
        } catch (Exception e) {
            log.error("Failed to parse chapter analysis result: title={}, rawLength={}", chapterTitle, rawJson.length(), e);
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
