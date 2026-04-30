package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.ParseRule;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ParseRuleMapper {

    @Select("SELECT * FROM parse_rule ORDER BY id")
    List<ParseRule> findAll();

    @Select("SELECT * FROM parse_rule WHERE id = #{id}")
    ParseRule findById(Long id);

    @Select("SELECT * FROM parse_rule WHERE enabled = 1")
    List<ParseRule> findEnabled();

    @Insert("INSERT INTO parse_rule (name, volume_regex, chapter_regex, enabled) " +
            "VALUES (#{name}, #{volumeRegex}, #{chapterRegex}, #{enabled})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ParseRule rule);

    @Update("UPDATE parse_rule SET name = #{name}, volume_regex = #{volumeRegex}, " +
            "chapter_regex = #{chapterRegex}, enabled = #{enabled} WHERE id = #{id}")
    int update(ParseRule rule);
}
