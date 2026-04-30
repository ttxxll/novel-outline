package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.SpiritHerbRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpiritHerbRecordMapper {

    @Insert("INSERT IGNORE INTO spirit_herb_record (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(SpiritHerbRecord record);

    @Select("SELECT * FROM spirit_herb_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<SpiritHerbRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM spirit_herb_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
