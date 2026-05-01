package com.noveloutline.web.controller;

import com.noveloutline.common.entity.Chapter;
import com.noveloutline.service.ChapterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ChapterService chapterService;

    @GetMapping("/{id}")
    public Chapter detail(@PathVariable Long id) {
        return chapterService.getById(id);
    }

    @GetMapping("/by-novel/{novelId}")
    public List<Chapter> byNovel(@PathVariable Long novelId) {
        return chapterService.getByNovelId(novelId);
    }
}
