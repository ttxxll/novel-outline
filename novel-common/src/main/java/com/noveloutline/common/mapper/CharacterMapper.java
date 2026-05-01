package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Character;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CharacterMapper {

    @Insert("INSERT IGNORE INTO characterCharacter (novel_id, first_chapter_id, name, role, relationship_to_protagonist) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{role}, #{relationshipToProtagonist})")
    int insertIgnore(Character record);

    @Select("SELECT * FROM characterCharacter WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<Character> findByNovelId(Long novelId);

    @Delete("DELETE FROM characterCharacter WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
