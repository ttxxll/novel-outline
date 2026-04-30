package com.noveloutline.common.entity;

import lombok.Data;

@Data
public class Chapter {

    private Long id;
    private Long volumeId;
    private Long novelId;
    private Integer index;
    private String title;
    private String rawContent;
    private String analysisResult;
    private String status = "PENDING";
    private Integer wordCount;
}
