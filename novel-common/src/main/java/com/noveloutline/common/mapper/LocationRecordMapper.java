package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.LocationRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LocationRecordMapper {

    @Insert("INSERT IGNORE INTO location_record (novel_id, first_chapter_id, name, type) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{type})")
    int insertIgnore(LocationRecord record);

    @Select("SELECT * FROM location_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<LocationRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM location_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
