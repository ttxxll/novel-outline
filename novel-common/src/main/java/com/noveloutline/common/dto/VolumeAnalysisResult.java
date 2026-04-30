package com.noveloutline.common.dto;

import java.util.List;

public class VolumeAnalysisResult {

    public String volumeSummary;
    public List<String> keyEvents;
    public List<ChapterAnalysisResult.CharacterEntry> charactersIntroduced;
    public List<ChapterAnalysisResult.FactionEntry> factionsIntroduced;
    public ChapterAnalysisResult.Items itemsAcquired;
    public List<ChapterAnalysisResult.LocationEntry> locationsVisited;
    public List<ChapterAnalysisResult.ConflictEntry> majorConflicts;
    public List<ChapterAnalysisResult.ForeshadowingEntry> foreshadowingPlanted;
    public List<ChapterAnalysisResult.ForeshadowingEntry> foreshadowingResolved;
}
