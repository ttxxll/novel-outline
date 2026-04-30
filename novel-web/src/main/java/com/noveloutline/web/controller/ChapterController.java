package com.noveloutline.web.controller;

import com.noveloutline.common.entity.Chapter;
import com.noveloutline.service.ChapterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @GetMapping("/{id}")
    public Chapter detail(@PathVariable Long id) {
        return chapterService.getById(id);
    }

    @GetMapping("/by-novel/{novelId}")
    public List<Chapter> byNovel(@PathVariable Long novelId) {
        return chapterService.getByNovelId(novelId);
    }
}
