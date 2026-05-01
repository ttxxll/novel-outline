package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Technique;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TechniqueMapper {

    @Insert("INSERT IGNORE INTO technique (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(Technique record);

    @Select("SELECT * FROM technique WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<Technique> findByNovelId(Long novelId);

    @Delete("DELETE FROM technique WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
