package com.noveloutline.service;

import com.noveloutline.common.dto.NovelListItem;
import com.noveloutline.common.dto.NovelProgress;
import com.noveloutline.common.entity.Chapter;
import com.noveloutline.common.entity.Novel;
import com.noveloutline.common.entity.ParseRule;
import com.noveloutline.common.entity.Volume;
import com.noveloutline.common.mapper.ChapterMapper;
import com.noveloutline.common.mapper.NovelMapper;
import com.noveloutline.common.mapper.NovelOutlineMapper;
import com.noveloutline.common.mapper.VolumeMapper;
import com.noveloutline.service.parser.NovelSplitter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NovelService {

    private final NovelMapper novelMapper;
    private final VolumeMapper volumeMapper;
    private final ChapterMapper chapterMapper;
    private final NovelOutlineMapper outlineMapper;
    private final ParseRuleService parseRuleService;

    public NovelService(NovelMapper novelMapper,
                        VolumeMapper volumeMapper,
                        ChapterMapper chapterMapper,
                        NovelOutlineMapper outlineMapper,
                        ParseRuleService parseRuleService) {
        this.novelMapper = novelMapper;
        this.volumeMapper = volumeMapper;
        this.chapterMapper = chapterMapper;
        this.outlineMapper = outlineMapper;
        this.parseRuleService = parseRuleService;
    }

    @Transactional
    public Novel uploadAndParse(MultipartFile file, Long parseRuleId) throws IOException {
        ParseRule rule = parseRuleService.getById(parseRuleId);

        Path tmpPath = Files.createTempFile("novel_", ".txt");
        file.transferTo(tmpPath.toFile());

        NovelSplitter splitter = new NovelSplitter();
        NovelSplitter.SplitResult splitResult = splitter.split(tmpPath, rule);

        Files.deleteIfExists(tmpPath);

        Novel novel = new Novel();
        novel.setTitle(extractTitle(file.getOriginalFilename()));
        novel.setOriginalFilename(file.getOriginalFilename());
        novel.setStatus("NOT_STARTED");
        novelMapper.insert(novel);

        Map<String, Long> volumeTitleToId = new LinkedHashMap<>();

        for (NovelSplitter.ChapterSegment seg : splitResult.segments) {
            Long volumeId = volumeTitleToId.computeIfAbsent(seg.volumeTitle, vt -> {
                Volume vol = new Volume();
                vol.setNovelId(novel.getId());
                vol.setIndex(volumeTitleToId.size());
                vol.setTitle(vt);
                volumeMapper.insert(vol);
                return vol.getId();
            });

            Chapter chapter = new Chapter();
            chapter.setNovelId(novel.getId());
            chapter.setVolumeId(volumeId);
            chapter.setIndex(0);
            chapter.setTitle(seg.title);
            chapter.setRawContent(seg.content);
            chapter.setStatus("PENDING");
            chapter.setWordCount(seg.content.length());
            chapterMapper.insert(chapter);
        }

        // Fix chapter indices to be sequential per volume
        List<Volume> volumes = volumeMapper.findByNovelId(novel.getId());
        for (Volume vol : volumes) {
            List<Chapter> chapters = chapterMapper.findByVolumeId(vol.getId());
            for (int i = 0; i < chapters.size(); i++) {
                chapterMapper.updateIndex(chapters.get(i).getId(), i);
            }
        }

        return novel;
    }

    public List<NovelListItem> listAll() {
        return novelMapper.findAll().stream()
                .map(n -> {
                    NovelListItem item = new NovelListItem();
                    item.id = n.getId();
                    item.title = n.getTitle();
                    item.status = n.getStatus();
                    item.totalChapters = (int) chapterMapper.countByNovelId(n.getId());
                    item.analyzedChapters = (int) chapterMapper.countByNovelIdAndStatus(n.getId(), "COMPLETED");
                    item.createdAt = n.getCreatedAt();
                    return item;
                })
                .collect(Collectors.toList());
    }

    public Novel getById(Long id) {
        Novel novel = novelMapper.findById(id);
        if (novel == null) {
            throw new NoSuchElementException("Novel not found: " + id);
        }
        return novel;
    }

    public NovelProgress getProgress(Long id) {
        Novel novel = getById(id);
        NovelProgress progress = new NovelProgress();
        progress.status = novel.getStatus();
        progress.totalChapters = (int) chapterMapper.countByNovelId(id);
        progress.chaptersDone = (int) chapterMapper.countByNovelIdAndStatus(id, "COMPLETED");
        return progress;
    }

    @Transactional
    public void delete(Long id) {
        outlineMapper.deleteByNovelId(id);
        chapterMapper.deleteByNovelId(id);
        volumeMapper.deleteByNovelId(id);
        novelMapper.deleteById(id);
    }

    private String extractTitle(String filename) {
        if (filename != null && !filename.trim().isEmpty()) {
            String name = filename;
            int dot = name.lastIndexOf('.');
            if (dot > 0) name = name.substring(0, dot);
            if (!name.trim().isEmpty()) return name;
        }
        return "未命名小说";
    }
}
