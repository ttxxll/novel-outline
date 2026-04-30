package com.noveloutline.common.dto;

import java.time.LocalDateTime;

public class NovelListItem {
    public Long id;
    public String title;
    public String status;
    public int totalChapters;
    public int analyzedChapters;
    public LocalDateTime createdAt;
}
