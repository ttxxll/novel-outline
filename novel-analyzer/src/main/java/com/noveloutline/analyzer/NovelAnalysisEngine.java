package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.dto.NovelContext;
import com.noveloutline.common.dto.OutlineResult;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import com.noveloutline.common.entity.Novel;
import com.noveloutline.common.entity.NovelOutline;
import com.noveloutline.common.entity.Volume;
import com.noveloutline.common.enums.NovelStatus;
import com.noveloutline.common.mapper.NovelMapper;
import com.noveloutline.common.mapper.NovelOutlineMapper;
import com.noveloutline.common.mapper.VolumeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class NovelAnalysisEngine {

    private static final Logger log = LoggerFactory.getLogger(NovelAnalysisEngine.class);

    private final VolumeAnalysisHandler volumeHandler;
    private final OutlineBuilder outlineBuilder;
    private final NovelContextManager contextManager;
    private final NovelMapper novelMapper;
    private final VolumeMapper volumeMapper;
    private final NovelOutlineMapper outlineMapper;
    private final ObjectMapper objectMapper;
    private final ThreadPoolExecutor analysisExecutor;

    public NovelAnalysisEngine(VolumeAnalysisHandler volumeHandler,
                               OutlineBuilder outlineBuilder,
                               NovelContextManager contextManager,
                               NovelMapper novelMapper,
                               VolumeMapper volumeMapper,
                               NovelOutlineMapper outlineMapper,
                               ObjectMapper objectMapper) {
        this.volumeHandler = volumeHandler;
        this.outlineBuilder = outlineBuilder;
        this.contextManager = contextManager;
        this.novelMapper = novelMapper;
        this.volumeMapper = volumeMapper;
        this.outlineMapper = outlineMapper;
        this.objectMapper = objectMapper;

        this.analysisExecutor = new ThreadPoolExecutor(
                5, 10,
                60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                r -> { Thread t = new Thread(r, "novel-analysis"); t.setDaemon(true); return t; },
                new ThreadPoolExecutor.CallerRunsPolicy());
        this.analysisExecutor.allowCoreThreadTimeOut(true);
    }

    /**
     * Validate, set status to ANALYZING, then submit actual work to the thread pool.
     */
    public void analyzeNovel(Long novelId) {
        Novel novel = novelMapper.findById(novelId);
        if (novel == null) {
            throw new IllegalArgumentException("Novel not found: " + novelId);
        }

        log.info("Starting analysis: novelId={}, title={}", novelId, novel.getTitle());
        novel.setStatus(NovelStatus.ANALYZING);
        novelMapper.update(novel);

        analysisExecutor.submit(() -> doAnalyzeNovel(novelId));
    }

    private void doAnalyzeNovel(Long novelId) {
        try {
            NovelContext context = contextManager.createInitial();
            List<Volume> volumes = volumeMapper.findByNovelId(novelId);
            log.info("Analyzing {} volumes", volumes.size());
            List<VolumeAnalysisResult> volumeResults = new ArrayList<>();

            for (int vi = 0; vi < volumes.size(); vi++) {
                Volume volume = volumes.get(vi);
                log.info("=== Volume {}/{}: '{}' ===", vi + 1, volumes.size(), volume.getTitle());

                VolumeAnalysisResult volResult = volumeHandler.analyze(volume, context);
                if (volResult == null) {
                    log.error("Analysis aborted at volume '{}' due to chapter failure", volume.getTitle());
                    markNovelFailed(novelId);
                    return;
                }
                volumeResults.add(volResult);
                contextManager.pruneAfterVolume(context, volResult.volumeSummary);
            }

            buildAndSaveOutline(novelId, volumeResults);
        } catch (Exception e) {
            log.error("Analysis failed for novel {}", novelId, e);
            markNovelFailed(novelId);
        }
    }

    private void buildAndSaveOutline(Long novelId, List<VolumeAnalysisResult> volumeResults) {
        Novel novel = novelMapper.findById(novelId);
        log.info("Building final outline...");
        try {
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

            novel.setStatus(NovelStatus.COMPLETED);
            novelMapper.update(novel);
            log.info("Analysis completed successfully: novelId={}, title={}", novelId, novel.getTitle());
        } catch (Exception e) {
            log.error("Failed to build outline for novel {}", novelId, e);
            markNovelFailed(novelId);
        }
    }

    private void markNovelFailed(Long novelId) {
        Novel novel = novelMapper.findById(novelId);
        if (novel != null) {
            novel.setStatus(NovelStatus.FAILED);
            novelMapper.update(novel);
        }
    }
}
