package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Location;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface LocationMapper {

    @Insert("INSERT IGNORE INTO location (novel_id, first_chapter_id, name, description) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{description})")
    int insertIgnore(Location record);

    @Select("SELECT * FROM location WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<Location> findByNovelId(Long novelId);

    @Delete("DELETE FROM location WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
