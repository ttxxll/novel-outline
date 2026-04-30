package com.noveloutline.service;

import com.noveloutline.analyzer.NovelAnalysisEngine;
import org.springframework.stereotype.Service;

@Service
public class AnalysisOrchestrator {

    private final NovelAnalysisEngine engine;

    public AnalysisOrchestrator(NovelAnalysisEngine engine) {
        this.engine = engine;
    }

    public void startAnalysis(Long novelId) {
        engine.analyzeNovel(novelId);
    }
}
