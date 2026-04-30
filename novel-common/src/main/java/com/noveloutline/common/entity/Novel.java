package com.noveloutline.common.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Novel {

    private Long id;
    private String title;
    private String originalFilename;
    private String status = "NOT_STARTED";
    private Long lastAnalyzedChapterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
