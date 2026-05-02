package com.noveloutline.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class OutlineResult {
    public String overallSummary;
    public String protagonistArc;
    public List<VolumeAnalysisResult> volumes;
    public List<String> foreshadowingChain;
    public List<String> conflictMap;
}
