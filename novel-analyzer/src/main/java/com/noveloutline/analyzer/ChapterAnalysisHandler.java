package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import com.noveloutline.common.entity.Chapter;
import com.noveloutline.common.entity.Novel;
import com.noveloutline.common.enums.ChapterStatus;
import com.noveloutline.common.mapper.ChapterMapper;
import com.noveloutline.common.mapper.NovelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChapterAnalysisHandler {

    private static final Logger log = LoggerFactory.getLogger(ChapterAnalysisHandler.class);
    private static final int MAX_RETRIES = 3;

    private final ChapterAnalyzer chapterAnalyzer;
    private final NovelContextManager contextManager;
    private final ChapterMapper chapterMapper;
    private final NovelMapper novelMapper;
    private final ObjectMapper objectMapper;

    public ChapterAnalysisHandler(ChapterAnalyzer chapterAnalyzer,
                                  NovelContextManager contextManager,
                                  ChapterMapper chapterMapper,
                                  NovelMapper novelMapper,
                                  ObjectMapper objectMapper) {
        this.chapterAnalyzer = chapterAnalyzer;
        this.contextManager = contextManager;
        this.chapterMapper = chapterMapper;
        this.novelMapper = novelMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * Analyze a single chapter with retry. On success the chapter is updated in DB,
     * context is updated, and progress is recorded. On failure the chapter is marked
     * FAILED and null is returned.
     *
     * @return result JSON string, or null if all retries exhausted
     */
    public String analyze(Chapter chapter, NovelContext context) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                ChapterAnalysisResult result = chapterAnalyzer.analyze(
                        chapter.getTitle(), chapter.getRawContent(), context);
                String resultJson = objectMapper.writeValueAsString(result);

                chapter.setAnalysisResult(resultJson);
                chapter.setStatus(ChapterStatus.COMPLETED);
                chapterMapper.update(chapter);

                updateProgress(chapter);
                contextManager.applyChapterResult(context, result, chapter.getIdx());
                return resultJson;
            } catch (Exception e) {
                log.error("Chapter {} '{}' attempt {}/{} error, ", chapter.getId(), chapter.getTitle(), attempt + 1, MAX_RETRIES, e);
                if (attempt < MAX_RETRIES - 1) {
                    sleep((long) Math.pow(2, attempt) * 2000);
                }
            }
        }

        log.error("Chapter {} '{}' failed after {} attempts", chapter.getId(), chapter.getTitle(), MAX_RETRIES);
        chapter.setStatus(ChapterStatus.FAILED);
        chapterMapper.update(chapter);
        return null;
    }

    private void updateProgress(Chapter chapter) {
        Novel novel = novelMapper.findById(chapter.getNovelId());
        if (novel != null) {
            novel.setLastAnalyzedChapterId(chapter.getId());
            novelMapper.update(novel);
        }
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
