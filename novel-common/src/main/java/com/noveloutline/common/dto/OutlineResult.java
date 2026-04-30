package com.noveloutline.common.dto;

import java.util.List;
import java.util.Map;

public class OutlineResult {

    public String overallSummary;
    public String protagonistArc;
    public List<VolumeAnalysisResult> volumes;
    public Map<String, List<ChapterAnalysisResult.CharacterEntry>> characterIndex;
    public Map<String, List<ChapterAnalysisResult.FactionEntry>> factionIndex;
    public Map<String, List<?>> itemIndex;
    public List<ChapterAnalysisResult.LocationEntry> locationTimeline;
    public List<ForeshadowingChainEntry> foreshadowingChain;
    public Map<String, List<ChapterAnalysisResult.ConflictEntry>> conflictMap;

    public static class ForeshadowingChainEntry {
        public String description;
        public String plantedChapter;
        public String resolvedChapter;
        public String resolution;
    }
}
