package com.noveloutline.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class VolumeAnalysisResult {
    public String volumeSummary;
    public List<String> keyEvents;
    public List<CharacterEntry> charactersIntroduced;
    public List<FactionEntry> factionsIntroduced;
    public Items itemsAcquired;
    public List<LocationEntry> locationsVisited;
    public List<ConflictEntry> majorConflicts;
    public List<ForeshadowingEntry> foreshadowingPlanted;
    public List<ForeshadowingEntry> foreshadowingResolved;
}
