package com.noveloutline.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChapterAnalysisResult {

    public String summary;
    public List<CharacterEntry> characters;
    public List<FactionEntry> factions;
    public Items items;
    public List<LocationEntry> locations;
    public List<ConflictEntry> conflicts;
    public List<ForeshadowingEntry> foreshadowing;
}
