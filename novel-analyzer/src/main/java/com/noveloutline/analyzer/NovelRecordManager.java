package com.noveloutline.analyzer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noveloutline.common.dto.*;
import com.noveloutline.common.entity.*;
import com.noveloutline.common.enums.ChapterStatus;
import com.noveloutline.common.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class NovelRecordManager {

    @Autowired
    private ChapterMapper chapterMapper;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CharacterMapper characterRecordMapper;
    @Autowired
    private WeaponMapper weaponRecordMapper;
    @Autowired
    private FactionMapper factionRecordMapper;
    @Autowired
    private SpiritBeastMapper spiritBeastRecordMapper;
    @Autowired
    private TechniqueMapper techniqueRecordMapper;
    @Autowired
    private SpiritHerbMapper spiritHerbRecordMapper;
    @Autowired
    private ElixirMapper elixirRecordMapper;
    @Autowired
    private LocationMapper locationRecordMapper;

    /**
     * Build records from all completed chapters of a novel.
     * Deletes existing records first so re-processing is safe.
     */
    public void saveRecordsFromNovel(Long novelId) {
        log.info("Building records from chapters: novelId={}", novelId);
        deleteByNovelId(novelId);
        List<Chapter> chapters = chapterMapper.findByNovelId(novelId);
        int total = 0;
        int chaptersProcessed = 0;
        for (Chapter chapter : chapters) {
            if (!ChapterStatus.COMPLETED.equals(chapter.getStatus()) || chapter.getAnalysisResult() == null) {
                continue;
            }
            ChapterAnalysisResult result = parseResult(chapter.getAnalysisResult());
            if (result == null) {
                continue;
            }
            total += saveOneChapter(novelId, chapter.getId(), result);
            chaptersProcessed++;
        }
        log.info("Records built: novelId={}, chaptersProcessed={}, totalRecords={}", novelId, chaptersProcessed, total);
    }

    public void deleteByNovelId(Long novelId) {
        characterRecordMapper.deleteByNovelId(novelId);
        weaponRecordMapper.deleteByNovelId(novelId);
        factionRecordMapper.deleteByNovelId(novelId);
        spiritBeastRecordMapper.deleteByNovelId(novelId);
        techniqueRecordMapper.deleteByNovelId(novelId);
        spiritHerbRecordMapper.deleteByNovelId(novelId);
        elixirRecordMapper.deleteByNovelId(novelId);
        locationRecordMapper.deleteByNovelId(novelId);
    }

    private ChapterAnalysisResult parseResult(String analysisResultJson) {
        try {
            return objectMapper.readValue(analysisResultJson, ChapterAnalysisResult.class);
        } catch (Exception e) {
            log.warn("Failed to parse chapter analysis result", e);
            return null;
        }
    }

    private int saveOneChapter(Long novelId, Long chapterId, ChapterAnalysisResult result) {
        int count = 0;
        count += saveCharacters(novelId, chapterId, result.characters);
        count += saveFactions(novelId, chapterId, result.factions);
        count += saveItems(novelId, chapterId, result.items);
        count += saveLocations(novelId, chapterId, result.locations);
        return count;
    }

    private int saveCharacters(Long novelId, Long chapterId, List<CharacterEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (CharacterEntry ch : entries) {
            if (ch.name == null || ch.name.isEmpty()) continue;
            NovelCharacter r = new NovelCharacter();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(ch.name);
            r.setRole(ch.role);
            r.setRelationshipToProtagonist(ch.relationshipToProtagonist);
            characterRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }

    private int saveFactions(Long novelId, Long chapterId, List<FactionEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (FactionEntry f : entries) {
            if (f.name == null || f.name.isEmpty()) continue;
            Faction r = new Faction();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(f.name);
            r.setType(f.type);
            r.setStanceTowardProtagonist(f.stanceTowardProtagonist);
            r.setDescription(f.description);
            factionRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }

    private int saveItems(Long novelId, Long chapterId, Items items) {
        if (items == null) return 0;
        int count = 0;
        count += saveWeapons(novelId, chapterId, items.weapons);
        count += saveTechniques(novelId, chapterId, items.techniques);
        count += saveElixirs(novelId, chapterId, items.elixirs);
        count += saveSpiritBeasts(novelId, chapterId, items.spiritBeasts);
        count += saveSpiritHerbs(novelId, chapterId, items.spiritHerbs);
        return count;
    }

    private int saveLocations(Long novelId, Long chapterId, List<LocationEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (LocationEntry loc : entries) {
            if (loc.name == null || loc.name.isEmpty()) continue;
            Location r = new Location();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(loc.name);
            r.setType(loc.type);
            locationRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }

    private int saveWeapons(Long novelId, Long chapterId, List<WeaponEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (WeaponEntry e : entries) {
            if (e.name == null || e.name.isEmpty()) continue;
            Weapon r = new Weapon();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(e.name);
            r.setGrade(e.grade);
            r.setSignificance(e.significance);
            weaponRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }

    private int saveTechniques(Long novelId, Long chapterId, List<TechniqueEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (TechniqueEntry e : entries) {
            if (e.name == null || e.name.isEmpty()) continue;
            Technique r = new Technique();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(e.name);
            r.setGrade(e.grade);
            r.setSignificance(e.significance);
            techniqueRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }

    private int saveElixirs(Long novelId, Long chapterId, List<ElixirEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (ElixirEntry e : entries) {
            if (e.name == null || e.name.isEmpty()) continue;
            Elixir r = new Elixir();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(e.name);
            r.setGrade(e.grade);
            r.setSignificance(e.significance);
            elixirRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }

    private int saveSpiritBeasts(Long novelId, Long chapterId, List<SpiritBeastEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (SpiritBeastEntry e : entries) {
            if (e.name == null || e.name.isEmpty()) continue;
            SpiritBeast r = new SpiritBeast();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(e.name);
            r.setLevel(e.level);
            r.setSignificance(e.significance);
            spiritBeastRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }

    private int saveSpiritHerbs(Long novelId, Long chapterId, List<SpiritHerbEntry> entries) {
        if (entries == null) return 0;
        int count = 0;
        for (SpiritHerbEntry e : entries) {
            if (e.name == null || e.name.isEmpty()) continue;
            SpiritHerb r = new SpiritHerb();
            r.setNovelId(novelId);
            r.setFirstChapterId(chapterId);
            r.setName(e.name);
            r.setGrade(e.grade);
            r.setSignificance(e.significance);
            spiritHerbRecordMapper.insertIgnore(r);
            count++;
        }
        return count;
    }
}
