package com.noveloutline.service;

import com.noveloutline.common.entity.Chapter;
import com.noveloutline.common.mapper.ChapterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChapterService {

    @Autowired
    private ChapterMapper chapterMapper;

    public Chapter getById(Long id) {
        Chapter chapter = chapterMapper.findById(id);
        if (chapter == null) {
            throw new RuntimeException("Chapter not found: " + id);
        }
        return chapter;
    }

    public List<Chapter> getByNovelId(Long novelId) {
        return chapterMapper.findByNovelId(novelId);
    }

    public List<Chapter> getByVolumeId(Long volumeId) {
        return chapterMapper.findByVolumeId(volumeId);
    }
}
