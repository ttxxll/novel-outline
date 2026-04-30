package com.noveloutline.web.controller;

import com.noveloutline.common.dto.NovelListItem;
import com.noveloutline.common.dto.NovelProgress;
import com.noveloutline.common.entity.Novel;
import com.noveloutline.service.AnalysisOrchestrator;
import com.noveloutline.service.NovelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

    private static final Logger log = LoggerFactory.getLogger(NovelController.class);

    private final NovelService novelService;
    private final AnalysisOrchestrator orchestrator;

    public NovelController(NovelService novelService, AnalysisOrchestrator orchestrator) {
        this.novelService = novelService;
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<Novel> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("parseRuleId") Long parseRuleId) throws IOException {
        log.info("Uploading novel: file={}, size={}bytes, parseRuleId={}",
                file.getOriginalFilename(), file.getSize(), parseRuleId);
        Novel novel = novelService.uploadAndParse(file, parseRuleId);
        log.info("Novel parsed: id={}, title={}, originalFilename={}", novel.getId(), novel.getTitle(), novel.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.CREATED).body(novel);
    }

    @GetMapping
    public List<NovelListItem> list() {
        log.debug("Listing all novels");
        return novelService.listAll();
    }

    @GetMapping("/{id}")
    public Novel detail(@PathVariable Long id) {
        log.debug("Get novel detail: id={}", id);
        return novelService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Deleting novel: id={}", id);
        novelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<String> startAnalysis(@PathVariable Long id) {
        log.info("Starting analysis for novel: id={}", id);
        orchestrator.startAnalysis(id);
        return ResponseEntity.accepted().body("Analysis started");
    }

    @GetMapping("/{id}/progress")
    public NovelProgress progress(@PathVariable Long id) {
        log.debug("Querying progress: novelId={}", id);
        return novelService.getProgress(id);
    }
}
