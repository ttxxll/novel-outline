package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.SpiritHerb;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpiritHerbMapper {

    @Insert("INSERT IGNORE INTO spirit_herb (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(SpiritHerb record);

    @Select("SELECT * FROM spirit_herb WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<SpiritHerb> findByNovelId(Long novelId);

    @Delete("DELETE FROM spirit_herb WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
