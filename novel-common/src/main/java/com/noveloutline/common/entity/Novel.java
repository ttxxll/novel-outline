package com.noveloutline.common.entity;

import com.noveloutline.common.enums.NovelStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Novel {

    private Long id;
    private String title;
    private String originalFilename;
    private NovelStatus status = NovelStatus.NOT_STARTED;
    private Long lastAnalyzedChapterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
