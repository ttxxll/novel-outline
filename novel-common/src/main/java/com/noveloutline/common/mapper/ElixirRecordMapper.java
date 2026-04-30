package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.ElixirRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ElixirRecordMapper {

    @Insert("INSERT IGNORE INTO elixir_record (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(ElixirRecord record);

    @Select("SELECT * FROM elixir_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<ElixirRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM elixir_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
