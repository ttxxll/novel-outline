package com.noveloutline.common.dto;

import java.util.ArrayList;
import java.util.List;

public class NovelContext {

    public String protagonist;

    public List<ChapterAnalysisResult.CharacterEntry> characters = new ArrayList<>();

    public List<ChapterAnalysisResult.FactionEntry> activeFactions = new ArrayList<>();

    public List<ChapterAnalysisResult.ForeshadowingEntry> unresolvedForeshadowing = new ArrayList<>();

    public List<ChapterAnalysisResult.ConflictEntry> activeConflicts = new ArrayList<>();

    public String recentSummary;

    public long totalChaptersAnalyzed;
}
