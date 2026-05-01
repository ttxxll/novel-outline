package com.noveloutline.service;

import com.noveloutline.common.entity.Volume;
import com.noveloutline.common.mapper.VolumeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolumeService {

    @Autowired
    private VolumeMapper volumeMapper;

    public Volume getById(Long id) {
        Volume volume = volumeMapper.findById(id);
        if (volume == null) {
            throw new RuntimeException("Volume not found: " + id);
        }
        return volume;
    }

    public List<Volume> getByNovelId(Long novelId) {
        return volumeMapper.findByNovelId(novelId);
    }
}
