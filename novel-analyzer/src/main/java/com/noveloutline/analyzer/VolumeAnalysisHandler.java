package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import com.noveloutline.common.entity.Chapter;
import com.noveloutline.common.entity.Volume;
import com.noveloutline.common.enums.ChapterStatus;
import com.noveloutline.common.mapper.ChapterMapper;
import com.noveloutline.common.mapper.VolumeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VolumeAnalysisHandler {

    private static final Logger log = LoggerFactory.getLogger(VolumeAnalysisHandler.class);

    private final ChapterAnalysisHandler chapterHandler;
    private final VolumeAggregator volumeAggregator;
    private final NovelContextManager contextManager;
    private final ChapterMapper chapterMapper;
    private final VolumeMapper volumeMapper;
    private final ObjectMapper objectMapper;

    public VolumeAnalysisHandler(ChapterAnalysisHandler chapterHandler,
                                 VolumeAggregator volumeAggregator,
                                 NovelContextManager contextManager,
                                 ChapterMapper chapterMapper,
                                 VolumeMapper volumeMapper,
                                 ObjectMapper objectMapper) {
        this.chapterHandler = chapterHandler;
        this.volumeAggregator = volumeAggregator;
        this.contextManager = contextManager;
        this.chapterMapper = chapterMapper;
        this.volumeMapper = volumeMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * Analyze all chapters in a volume sequentially, then aggregate.
     * Supports resume: skips chapters already marked COMPLETED.
     *
     * @return volume analysis result, or null if a chapter failed and processing should stop
     */
    public VolumeAnalysisResult analyze(Volume volume, NovelContext context) {
        List<Chapter> chapters = chapterMapper.findByVolumeId(volume.getId());
        log.info("Volume '{}': {} chapters to analyze", volume.getTitle(), chapters.size());
        List<String> chapterResultJsons = new ArrayList<>();

        for (int ci = 0; ci < chapters.size(); ci++) {
            Chapter chapter = chapters.get(ci);

            if (ChapterStatus.COMPLETED.equals(chapter.getStatus()) && chapter.getAnalysisResult() != null) {
                log.debug("Chapter {}/{} '{}' already analyzed, restoring context",
                        ci + 1, chapters.size(), chapter.getTitle());
                restoreContext(chapter, context);
                chapterResultJsons.add(chapter.getAnalysisResult());
                continue;
            }

            log.info("=== Chapter {}/{}: '{}' ({} words) ===", ci + 1, chapters.size(), chapter.getTitle(), chapter.getWordCount());

            String resultJson = chapterHandler.analyze(chapter, context);
            if (resultJson == null) {
                return null;
            }
            chapterResultJsons.add(resultJson);
        }

        try {
            VolumeAnalysisResult volResult = volumeAggregator.aggregate(volume.getTitle(), chapterResultJsons);
            volume.setSummary(objectMapper.writeValueAsString(volResult));
            volumeMapper.update(volume);
            return volResult;
        } catch (Exception e) {
            log.error("Failed to aggregate volume '{}'", volume.getTitle(), e);
            return null;
        }
    }

    private void restoreContext(Chapter chapter, NovelContext context) {
        try {
            ChapterAnalysisResult existing = objectMapper.readValue(
                    chapter.getAnalysisResult(), ChapterAnalysisResult.class);
            contextManager.applyChapterResult(context, existing, chapter.getIdx());
        } catch (Exception e) {
            log.warn("Failed to restore context from chapter {}", chapter.getId());
        }
    }
}
