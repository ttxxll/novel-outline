package com.noveloutline.web.controller;

import com.noveloutline.common.entity.Volume;
import com.noveloutline.service.VolumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/volumes")
public class VolumeController {

    @Autowired
    private VolumeService volumeService;

    @GetMapping("/{id}")
    public Volume detail(@PathVariable Long id) {
        return volumeService.getById(id);
    }

    @GetMapping("/by-novel/{novelId}")
    public List<Volume> byNovel(@PathVariable Long novelId) {
        return volumeService.getByNovelId(novelId);
    }
}
