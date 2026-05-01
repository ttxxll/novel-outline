package com.noveloutline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.client.AiClient;
import com.noveloutline.common.dto.ChapterAnalysisResult;
import com.noveloutline.common.dto.NovelContext;
import com.noveloutline.common.dto.OutlineResult;
import com.noveloutline.common.dto.VolumeAnalysisResult;
import com.noveloutline.common.entity.Chapter;
import com.noveloutline.common.entity.Novel;
import com.noveloutline.common.entity.NovelOutline;
import com.noveloutline.common.entity.Volume;
import com.noveloutline.common.enums.ChapterStatus;
import com.noveloutline.common.enums.NovelStatus;
import com.noveloutline.common.mapper.ChapterMapper;
import com.noveloutline.common.mapper.NovelMapper;
import com.noveloutline.common.mapper.NovelOutlineMapper;
import com.noveloutline.common.mapper.VolumeMapper;
import com.noveloutline.prompt.ChapterPrompt;
import com.noveloutline.prompt.OutlinePrompt;
import com.noveloutline.prompt.VolumePrompt;
import com.noveloutline.util.AnalysisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AnalysisService {

    private static final int MAX_RETRIES = 3;
    private final ThreadPoolExecutor executor = createExecutor();

    @Autowired
    private AiClient aiClient;
    @Autowired
    private NovelMapper novelMapper;
    @Autowired
    private VolumeMapper volumeMapper;
    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private NovelOutlineMapper outlineMapper;
    @Autowired
    private ObjectMapper objectMapper;

    public void startAnalysis(Long novelId) {
        Novel novel = novelMapper.findById(novelId);
        if (novel == null) {
            throw new IllegalArgumentException("Novel not found: " + novelId);
        }
        log.info("Starting analysis: novelId={}, title={}", novelId, novel.getTitle());
        novel.setStatus(NovelStatus.ANALYZING);
        novelMapper.update(novel);
        executor.submit(() -> doAnalyzeNovel(novelId));
    }

    private void doAnalyzeNovel(Long novelId) {
        try {
            NovelContext context = new NovelContext();
            List<Volume> volumes = volumeMapper.findByNovelId(novelId);
            log.info("Analyzing {} volumes", volumes.size());
            List<VolumeAnalysisResult> volumeResults = new ArrayList<>();
            for (int vi = 0; vi < volumes.size(); vi++) {
                Volume volume = volumes.get(vi);
                log.info("=== Volume {}/{}: '{}' ===", vi + 1, volumes.size(), volume.getTitle());
                VolumeAnalysisResult volResult = analyzeVolume(volume, context);
                if (volResult == null) {
                    log.error("Analysis aborted at volume '{}' due to chapter failure", volume.getTitle());
                    markNovelFailed(novelId);
                    return;
                }
                volumeResults.add(volResult);
                AnalysisUtil.pruneAfterVolume(context, volResult.volumeSummary);
            }
            saveOutline(novelId, volumeResults);
        } catch (Exception e) {
            log.error("Analysis failed for novel {}", novelId, e);
            markNovelFailed(novelId);
        }
    }

    private VolumeAnalysisResult analyzeVolume(Volume volume, NovelContext context) {
        List<Chapter> chapters = chapterMapper.findByVolumeId(volume.getId());
        log.info("Volume '{}': {} chapters to analyze", volume.getTitle(), chapters.size());
        List<String> chapterJsons = new ArrayList<>();
        for (int ci = 0; ci < chapters.size(); ci++) {
            Chapter chapter = chapters.get(ci);
            if (ChapterStatus.COMPLETED.equals(chapter.getStatus()) && chapter.getAnalysisResult() != null) {
                log.debug("Chapter {}/{} '{}' already analyzed, restoring context", ci + 1, chapters.size(), chapter.getTitle());
                restoreContext(chapter, context);
                chapterJsons.add(chapter.getAnalysisResult());
                continue;
            }
            log.info("=== Chapter {}/{}: '{}' ({} words) ===", ci + 1, chapters.size(), chapter.getTitle(), chapter.getWordCount());
            String resultJson = analyzeChapter(chapter, context);
            if (resultJson == null) {
                return null;
            }
            chapterJsons.add(resultJson);
        }
        try {
            VolumeAnalysisResult volResult = aggregateVolume(volume.getTitle(), chapterJsons);
            volume.setSummary(objectMapper.writeValueAsString(volResult));
            volumeMapper.update(volume);
            return volResult;
        } catch (Exception e) {
            log.error("Failed to aggregate volume '{}'", volume.getTitle(), e);
            return null;
        }
    }

    private String analyzeChapter(Chapter chapter, NovelContext context) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                ChapterAnalysisResult result = callAiForChapter(chapter.getTitle(), chapter.getRawContent(), context);
                String resultJson = objectMapper.writeValueAsString(result);
                chapter.setAnalysisResult(resultJson);
                chapter.setStatus(ChapterStatus.COMPLETED);
                chapterMapper.update(chapter);
                updateProgress(chapter);
                AnalysisUtil.applyChapterResult(context, result, chapter.getIdx());
                return resultJson;
            } catch (Exception e) {
                log.error("Chapter {} '{}' attempt {}/{} error", chapter.getId(), chapter.getTitle(), attempt + 1, MAX_RETRIES, e);
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

    private ChapterAnalysisResult callAiForChapter(String title, String content, NovelContext context) {
        log.info("Analyzing chapter: title={}, contentLength={}, totalChaptersAnalyzed={}", title, content.length(), context.totalChaptersAnalyzed);
        try {
            String rawJson = aiClient.chat(ChapterPrompt.systemPrompt(), ChapterPrompt.userMessage(title, content, context));
            rawJson = AnalysisUtil.extractJson(rawJson);
            log.info("callAiForChapter rawJson = {}", rawJson);
            return objectMapper.readValue(rawJson, ChapterAnalysisResult.class);
        } catch (Exception e) {
            log.error("Failed to parse chapter analysis result: title={}", title, e);
            throw new RuntimeException("Failed to parse chapter analysis result", e);
        }
    }

    private VolumeAnalysisResult aggregateVolume(String title, List<String> chapterJsons) {
        log.info("Aggregating volume: title={}, chapterCount={}", title, chapterJsons.size());
        try {
            String chaptersJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(chapterJsons);
            String rawJson = aiClient.chat(VolumePrompt.systemPrompt(), VolumePrompt.userMessage(title, chaptersJson));
            rawJson = AnalysisUtil.extractJson(rawJson);
            return objectMapper.readValue(rawJson, VolumeAnalysisResult.class);
        } catch (Exception e) {
            log.error("Failed to aggregate volume: title={}", title, e);
            throw new RuntimeException("Failed to aggregate volume", e);
        }
    }

    private OutlineResult buildOutline(String title, List<VolumeAnalysisResult> volumeResults) {
        log.info("Building outline: novelTitle={}, volumeCount={}", title, volumeResults.size());
        try {
            String volumesJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(volumeResults);
            String rawJson = aiClient.chat(OutlinePrompt.systemPrompt(), OutlinePrompt.userMessage(title, volumesJson));
            rawJson = AnalysisUtil.extractJson(rawJson);
            return objectMapper.readValue(rawJson, OutlineResult.class);
        } catch (Exception e) {
            log.error("Failed to build outline: novelTitle={}", title, e);
            throw new RuntimeException("Failed to build outline", e);
        }
    }

    private void saveOutline(Long novelId, List<VolumeAnalysisResult> volumeResults) {
        Novel novel = novelMapper.findById(novelId);
        log.info("Building final outline...");
        try {
            OutlineResult outline = buildOutline(novel.getTitle(), volumeResults);
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

    private void restoreContext(Chapter chapter, NovelContext context) {
        try {
            ChapterAnalysisResult existing = objectMapper.readValue(chapter.getAnalysisResult(), ChapterAnalysisResult.class);
            AnalysisUtil.applyChapterResult(context, existing, chapter.getIdx());
        } catch (Exception e) {
            log.warn("Failed to restore context from chapter {}", chapter.getId());
        }
    }

    private void updateProgress(Chapter chapter) {
        Novel novel = novelMapper.findById(chapter.getNovelId());
        if (novel != null) {
            novel.setLastAnalyzedChapterId(chapter.getId());
            novelMapper.update(novel);
        }
    }

    private void markNovelFailed(Long novelId) {
        Novel novel = novelMapper.findById(novelId);
        if (novel != null) {
            novel.setStatus(NovelStatus.FAILED);
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

    private static ThreadPoolExecutor createExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                5, 10, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                r -> { Thread t = new Thread(r, "novel-analysis"); t.setDaemon(true); return t; },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        executor.allowCoreThreadTimeOut(false);
        return executor;
    }
}
