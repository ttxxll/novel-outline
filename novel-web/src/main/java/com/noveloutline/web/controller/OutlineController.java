package com.noveloutline.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.entity.NovelOutline;
import com.noveloutline.common.mapper.NovelOutlineMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/novels/{novelId}/outline")
@Slf4j
public class OutlineController {

    @Autowired
    private NovelOutlineMapper outlineMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @GetMapping
    public Map<String, Object> getOutline(@PathVariable Long novelId) {
        log.debug("Get outline: novelId={}", novelId);
        NovelOutline outline = outlineMapper.findByNovelId(novelId);
        if (outline == null) {
            log.info("Outline not yet generated: novelId={}", novelId);
            return Collections.singletonMap("message", "Outline not yet generated");
        }
        try {
            return objectMapper.readValue(outline.getOutlineJson(), Map.class);
        } catch (Exception e) {
            log.error("Failed to parse outline JSON: novelId={}", novelId, e);
            return Collections.singletonMap("error", "Failed to parse outline JSON");
        }
    }
}
