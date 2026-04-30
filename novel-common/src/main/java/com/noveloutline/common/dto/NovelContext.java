package com.noveloutline.common.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NovelContext {

    public String protagonist;

    public List<CharacterEntry> characters = new ArrayList<>();

    public List<FactionEntry> activeFactions = new ArrayList<>();

    public List<ForeshadowingEntry> unresolvedForeshadowing = new ArrayList<>();

    public List<ConflictEntry> activeConflicts = new ArrayList<>();

    public String recentSummary;

    public long totalChaptersAnalyzed;
}
