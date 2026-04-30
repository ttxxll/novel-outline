package com.noveloutline.common.entity;

import lombok.Data;

@Data
public class ParseRule {

    private Long id;
    private String name;
    private String volumeRegex;
    private String chapterRegex;
    private Boolean enabled = true;
}
