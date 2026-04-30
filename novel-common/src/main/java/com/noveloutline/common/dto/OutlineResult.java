package com.noveloutline.common.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OutlineResult {
    public String overallSummary;
    public String protagonistArc;
    public List<VolumeAnalysisResult> volumes;
    public Map<String, List<CharacterEntry>> characterIndex;
    public Map<String, List<FactionEntry>> factionIndex;
    public Map<String, List<?>> itemIndex;
    public List<LocationEntry> locationTimeline;
    public List<ForeshadowingChainEntry> foreshadowingChain;
    public Map<String, List<ConflictEntry>> conflictMap;
}
