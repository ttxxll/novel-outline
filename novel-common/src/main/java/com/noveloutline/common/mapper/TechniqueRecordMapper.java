package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.TechniqueRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TechniqueRecordMapper {

    @Insert("INSERT IGNORE INTO technique_record (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(TechniqueRecord record);

    @Select("SELECT * FROM technique_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<TechniqueRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM technique_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
