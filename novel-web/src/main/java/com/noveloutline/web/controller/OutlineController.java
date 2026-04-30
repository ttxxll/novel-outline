package com.noveloutline.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.entity.NovelOutline;
import com.noveloutline.common.mapper.NovelOutlineMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/novels/{novelId}/outline")
public class OutlineController {

    private final NovelOutlineMapper outlineMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OutlineController(NovelOutlineMapper outlineMapper) {
        this.outlineMapper = outlineMapper;
    }

    @GetMapping
    public Map<String, Object> getOutline(@PathVariable Long novelId) {
        NovelOutline outline = outlineMapper.findByNovelId(novelId);
        if (outline == null) {
            return Collections.singletonMap("message", "Outline not yet generated");
        }
        try {
            return objectMapper.readValue(outline.getOutlineJson(), Map.class);
        } catch (Exception e) {
            return Collections.singletonMap("error", "Failed to parse outline JSON");
        }
    }
}
