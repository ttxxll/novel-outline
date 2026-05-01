package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.NovelCharacter;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CharacterMapper {

    @Insert("INSERT IGNORE INTO characterCharacter (novel_id, first_chapter_id, name, role, relationship_to_protagonist) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{role}, #{relationshipToProtagonist})")
    int insertIgnore(NovelCharacter record);

    @Select("SELECT * FROM characterCharacter WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<NovelCharacter> findByNovelId(Long novelId);

    @Delete("DELETE FROM characterCharacter WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
