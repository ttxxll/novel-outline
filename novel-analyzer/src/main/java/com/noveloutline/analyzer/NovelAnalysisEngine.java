package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import com.noveloutline.common.dto.OutlineResult;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import com.noveloutline.common.entity.Chapter;
import com.noveloutline.common.entity.Novel;
import com.noveloutline.common.entity.NovelOutline;
import com.noveloutline.common.entity.Volume;
import com.noveloutline.common.mapper.ChapterMapper;
import com.noveloutline.common.mapper.NovelMapper;
import com.noveloutline.common.mapper.NovelOutlineMapper;
import com.noveloutline.common.mapper.VolumeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class NovelAnalysisEngine {

    private static final Logger log = LoggerFactory.getLogger(NovelAnalysisEngine.class);
    private static final int MAX_RETRIES = 3;

    private final ChapterAnalyzer chapterAnalyzer;
    private final VolumeAggregator volumeAggregator;
    private final OutlineBuilder outlineBuilder;
    private final NovelContextManager contextManager;
    private final ChapterMapper chapterMapper;
    private final VolumeMapper volumeMapper;
    private final NovelMapper novelMapper;
    private final NovelOutlineMapper outlineMapper;
    private final ObjectMapper objectMapper;

    public NovelAnalysisEngine(ChapterAnalyzer chapterAnalyzer,
                               VolumeAggregator volumeAggregator,
                               OutlineBuilder outlineBuilder,
                               NovelContextManager contextManager,
                               ChapterMapper chapterMapper,
                               VolumeMapper volumeMapper,
                               NovelMapper novelMapper,
                               NovelOutlineMapper outlineMapper,
                               ObjectMapper objectMapper) {
        this.chapterAnalyzer = chapterAnalyzer;
        this.volumeAggregator = volumeAggregator;
        this.outlineBuilder = outlineBuilder;
        this.contextManager = contextManager;
        this.chapterMapper = chapterMapper;
        this.volumeMapper = volumeMapper;
        this.novelMapper = novelMapper;
        this.outlineMapper = outlineMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void analyzeNovel(Long novelId) {
        Novel novel = novelMapper.findById(novelId);
        if (novel == null) {
            throw new IllegalArgumentException("Novel not found: " + novelId);
        }

        novel.setStatus("ANALYZING");
        novelMapper.update(novel);

        try {
            NovelContext context = contextManager.createInitial();
            List<Volume> volumes = volumeMapper.findByNovelId(novelId);
            List<VolumeAnalysisResult> volumeResults = new ArrayList<>();

            for (Volume volume : volumes) {
                VolumeAnalysisResult volResult = analyzeVolume(volume, context);
                volumeResults.add(volResult);

                volume.setSummary(objectMapper.writeValueAsString(volResult));
                volumeMapper.update(volume);

                contextManager.pruneAfterVolume(context, volResult.volumeSummary);
            }

            OutlineResult outline = outlineBuilder.build(novel.getTitle(), volumeResults);

            NovelOutline outlineEntity = outlineMapper.findByNovelId(novelId);
            String outlineJson = objectMapper.writeValueAsString(outline);
            if (outlineEntity == null) {
                outlineEntity = new NovelOutline();
                outlineEntity.setNovelId(novelId);
                outlineEntity.setOutlineJson(outlineJson);
                outlineMapper.insert(outlineEntity);
            } else {
                outlineMapper.updateByNovelId(novelId, outlineJson);
            }

            novel.setStatus("COMPLETED");
            novelMapper.update(novel);

        } catch (Exception e) {
            log.error("Analysis failed for novel {}", novelId, e);
            novel.setStatus("FAILED");
            novelMapper.update(novel);
            throw new RuntimeException("Analysis failed", e);
        }
    }

    private VolumeAnalysisResult analyzeVolume(Volume volume, NovelContext context) throws Exception {
        List<Chapter> chapters = chapterMapper.findByVolumeId(volume.getId());
        List<String> chapterResultJsons = new ArrayList<>();

        for (Chapter chapter : chapters) {
            if ("COMPLETED".equals(chapter.getStatus()) && chapter.getAnalysisResult() != null) {
                try {
                    ChapterAnalysisResult existing = objectMapper.readValue(
                            chapter.getAnalysisResult(), ChapterAnalysisResult.class);
                    contextManager.applyChapterResult(context, existing, chapter.getIndex());
                    chapterResultJsons.add(chapter.getAnalysisResult());
                } catch (Exception e) {
                    log.warn("Failed to restore context from chapter {}", chapter.getId());
                }
                continue;
            }

            ChapterAnalysisResult result = analyzeChapterWithRetry(chapter, context);
            String resultJson = objectMapper.writeValueAsString(result);

            chapter.setAnalysisResult(resultJson);
            chapter.setStatus("COMPLETED");
            chapterMapper.update(chapter);

            Novel novel = novelMapper.findById(volume.getNovelId());
            if (novel != null) {
                novel.setLastAnalyzedChapterId(chapter.getId());
                novelMapper.update(novel);
            }

            contextManager.applyChapterResult(context, result, chapter.getIndex());
            chapterResultJsons.add(resultJson);
        }

        return volumeAggregator.aggregate(volume.getTitle(), chapterResultJsons);
    }

    private ChapterAnalysisResult analyzeChapterWithRetry(Chapter chapter, NovelContext context) throws Exception {
        Exception lastException = null;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return chapterAnalyzer.analyze(chapter.getTitle(), chapter.getRawContent(), context);
            } catch (Exception e) {
                lastException = e;
                log.warn("Chapter {} attempt {} failed: {}", chapter.getId(), attempt + 1, e.getMessage());
                if (attempt < MAX_RETRIES - 1) {
                    long delay = (long) Math.pow(2, attempt) * 2000;
                    Thread.sleep(delay);
                }
            }
        }
        chapter.setStatus("FAILED");
        chapterMapper.update(chapter);
        throw lastException;
    }
}
