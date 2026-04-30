package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.NovelOutline;
import org.apache.ibatis.annotations.*;

@Mapper
public interface NovelOutlineMapper {

    @Select("SELECT * FROM novel_outline WHERE id = #{id}")
    NovelOutline findById(Long id);

    @Select("SELECT * FROM novel_outline WHERE novel_id = #{novelId}")
    NovelOutline findByNovelId(Long novelId);

    @Insert("INSERT INTO novel_outline (novel_id, outline_json) VALUES (#{novelId}, #{outlineJson})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NovelOutline outline);

    @Update("UPDATE novel_outline SET outline_json = #{outlineJson} WHERE novel_id = #{novelId}")
    int updateByNovelId(@Param("novelId") Long novelId, @Param("outlineJson") String outlineJson);

    @Delete("DELETE FROM novel_outline WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
