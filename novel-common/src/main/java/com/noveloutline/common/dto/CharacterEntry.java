package com.noveloutline.common.dto;

import lombok.Data;

@Data
public class CharacterEntry {
    public String name;
    public String role;
    public String relationshipToProtagonist;
    public String action;
    public int lastSeenChapter;
}
