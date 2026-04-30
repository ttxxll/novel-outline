package com.noveloutline.common.dto;

import com.noveloutline.common.enums.NovelStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NovelListItem {
    public Long id;
    public String title;
    public NovelStatus status;
    public int totalChapters;
    public int analyzedChapters;
    public LocalDateTime createdAt;
}
