package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.CharacterRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CharacterRecordMapper {

    @Insert("INSERT IGNORE INTO character_record (novel_id, first_chapter_id, name, role, relationship_to_protagonist) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{role}, #{relationshipToProtagonist})")
    int insertIgnore(CharacterRecord record);

    @Select("SELECT * FROM character_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<CharacterRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM character_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
