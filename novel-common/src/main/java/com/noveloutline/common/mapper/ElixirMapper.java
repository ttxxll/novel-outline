package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Elixir;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ElixirMapper {

    @Insert("INSERT IGNORE INTO elixir (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(Elixir record);

    @Select("SELECT * FROM elixir WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<Elixir> findByNovelId(Long novelId);

    @Delete("DELETE FROM elixir WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
