package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.FactionRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FactionRecordMapper {

    @Insert("INSERT IGNORE INTO faction_record (novel_id, first_chapter_id, name, type, stance_toward_protagonist, description) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{type}, #{stanceTowardProtagonist}, #{description})")
    int insertIgnore(FactionRecord record);

    @Select("SELECT * FROM faction_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<FactionRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM faction_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
