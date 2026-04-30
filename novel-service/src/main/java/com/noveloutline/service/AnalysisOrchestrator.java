package com.noveloutline.service;

import com.noveloutline.analyzer.NovelAnalysisEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AnalysisOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AnalysisOrchestrator.class);
    private final NovelAnalysisEngine engine;

    public AnalysisOrchestrator(NovelAnalysisEngine engine) {
        this.engine = engine;
    }

    @Async
    public void startAnalysis(Long novelId) {
        log.info("Async analysis started: novelId={}", novelId);
        try {
            engine.analyzeNovel(novelId);
        } catch (Exception e) {
            log.error("Analysis failed for novel {}", novelId, e);
        }
    }
}
