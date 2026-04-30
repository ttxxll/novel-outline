package com.noveloutline.common.dto;

import com.noveloutline.common.enums.NovelStatus;
import lombok.Data;

@Data
public class NovelProgress {
    public NovelStatus status;
    public int chaptersDone;
    public int totalChapters;
    public String currentChapterTitle;
}
