package com.noveloutline.service;

import com.noveloutline.analyzer.NovelAnalysisEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnalysisOrchestrator {

    @Autowired
    private NovelAnalysisEngine engine;

    public void startAnalysis(Long novelId) {
        engine.analyzeNovel(novelId);
    }
}
