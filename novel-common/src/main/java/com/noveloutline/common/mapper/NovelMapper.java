package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Novel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NovelMapper {

    @Select("SELECT * FROM novel WHERE id = #{id}")
    Novel findById(Long id);

    @Select("SELECT * FROM novel ORDER BY created_at DESC")
    List<Novel> findAll();

    @Insert("INSERT INTO novel (title, original_filename, status, last_analyzed_chapter_id) " +
            "VALUES (#{title}, #{originalFilename}, #{status}, #{lastAnalyzedChapterId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Novel novel);

    @Update("UPDATE novel SET title = #{title}, original_filename = #{originalFilename}, " +
            "status = #{status}, last_analyzed_chapter_id = #{lastAnalyzedChapterId} WHERE id = #{id}")
    int update(Novel novel);

    @Delete("DELETE FROM novel WHERE id = #{id}")
    int deleteById(Long id);
}
