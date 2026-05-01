package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.SpiritBeast;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpiritBeastMapper {

    @Insert("INSERT IGNORE INTO spirit_beast (novel_id, first_chapter_id, name, level, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{level}, #{significance})")
    int insertIgnore(SpiritBeast record);

    @Select("SELECT * FROM spirit_beast WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<SpiritBeast> findByNovelId(Long novelId);

    @Delete("DELETE FROM spirit_beast WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
