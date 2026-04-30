package com.noveloutline.common.entity;

import com.noveloutline.common.enums.ChapterStatus;
import lombok.Data;

@Data
public class Chapter {

    private Long id;
    private Long volumeId;
    private Long novelId;
    private Integer idx;
    private String title;
    private String rawContent;
    private String analysisResult;
    private ChapterStatus status = ChapterStatus.PENDING;
    private Integer wordCount;
}
