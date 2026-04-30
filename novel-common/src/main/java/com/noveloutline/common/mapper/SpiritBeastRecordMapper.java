package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.SpiritBeastRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SpiritBeastRecordMapper {

    @Insert("INSERT IGNORE INTO spirit_beast_record (novel_id, first_chapter_id, name, level, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{level}, #{significance})")
    int insertIgnore(SpiritBeastRecord record);

    @Select("SELECT * FROM spirit_beast_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<SpiritBeastRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM spirit_beast_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
