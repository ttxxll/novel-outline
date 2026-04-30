package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.analyzer.client.DeepSeekClient;
import com.noveloutline.analyzer.prompt.ChapterPrompt;
import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChapterAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(ChapterAnalyzer.class);

    private final DeepSeekClient deepSeekClient;
    private final ObjectMapper objectMapper;

    public ChapterAnalyzer(DeepSeekClient deepSeekClient, ObjectMapper objectMapper) {
        this.deepSeekClient = deepSeekClient;
        this.objectMapper = objectMapper;
    }

    public ChapterAnalysisResult analyze(String chapterTitle, String chapterContent, NovelContext context) {
        log.info("Analyzing chapter: title={}, contentLength={}, totalChaptersAnalyzed={}",
                chapterTitle, chapterContent.length(), context.totalChaptersAnalyzed);

        String systemPrompt = ChapterPrompt.systemPrompt();
        String userMessage = ChapterPrompt.userMessage(chapterTitle, chapterContent, context);

        String rawJson = deepSeekClient.chat(systemPrompt, userMessage);
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
