package com.noveloutline.web.controller;

import com.noveloutline.common.dto.NovelListItem;
import com.noveloutline.common.dto.NovelProgress;
import com.noveloutline.common.entity.Novel;
import com.noveloutline.service.AnalysisOrchestrator;
import com.noveloutline.service.NovelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/novels")
public class NovelController {

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
        Novel novel = novelService.uploadAndParse(file, parseRuleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(novel);
    }

    @GetMapping
    public List<NovelListItem> list() {
        return novelService.listAll();
    }

    @GetMapping("/{id}")
    public Novel detail(@PathVariable Long id) {
        return novelService.getById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        novelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<String> startAnalysis(@PathVariable Long id) {
        orchestrator.startAnalysis(id);
        return ResponseEntity.accepted().body("Analysis started");
    }

    @GetMapping("/{id}/progress")
    public NovelProgress progress(@PathVariable Long id) {
        return novelService.getProgress(id);
    }
}
