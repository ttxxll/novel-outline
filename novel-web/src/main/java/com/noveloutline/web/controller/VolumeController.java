package com.noveloutline.web.controller;

import com.noveloutline.common.entity.Volume;
import com.noveloutline.service.VolumeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volumes")
public class VolumeController {

    private final VolumeService volumeService;

    public VolumeController(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    @GetMapping("/{id}")
    public Volume detail(@PathVariable Long id) {
        return volumeService.getById(id);
    }

    @GetMapping("/by-novel/{novelId}")
    public List<Volume> byNovel(@PathVariable Long novelId) {
        return volumeService.getByNovelId(novelId);
    }
}
