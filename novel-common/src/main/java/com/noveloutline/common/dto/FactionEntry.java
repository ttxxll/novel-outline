package com.noveloutline.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class FactionEntry {
    public String name;
    public String type;
    public String stanceTowardProtagonist;
    public List<String> members;
    public String description;
}
