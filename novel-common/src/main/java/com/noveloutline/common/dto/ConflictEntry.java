package com.noveloutline.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class ConflictEntry {
    public String type;
    public List<String> parties;
    public String cause;
    public String result;
}
