package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Chapter;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChapterMapper {

    @Select("SELECT * FROM chapter WHERE id = #{id}")
    Chapter findById(Long id);

    @Select("SELECT * FROM chapter WHERE novel_id = #{novelId} ORDER BY idx ASC")
    List<Chapter> findByNovelId(Long novelId);

    @Select("SELECT * FROM chapter WHERE volume_id = #{volumeId} ORDER BY idx ASC")
    List<Chapter> findByVolumeId(Long volumeId);

    @Select("SELECT * FROM chapter WHERE novel_id = #{novelId} AND status = #{status} ORDER BY idx ASC")
    List<Chapter> findByNovelIdAndStatus(@Param("novelId") Long novelId, @Param("status") String status);

    @Select("SELECT COUNT(*) FROM chapter WHERE novel_id = #{novelId}")
    long countByNovelId(Long novelId);

    @Select("SELECT COUNT(*) FROM chapter WHERE novel_id = #{novelId} AND status = #{status}")
    long countByNovelIdAndStatus(@Param("novelId") Long novelId, @Param("status") String status);

    @Insert("INSERT INTO chapter (volume_id, novel_id, idx, title, raw_content, analysis_result, status, word_count) " +
            "VALUES (#{volumeId}, #{novelId}, #{index}, #{title}, #{rawContent}, #{analysisResult}, #{status}, #{wordCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Chapter chapter);

    @Update("UPDATE chapter SET analysis_result = #{analysisResult}, status = #{status}, title = #{title} WHERE id = #{id}")
    int update(Chapter chapter);

    @Update("UPDATE chapter SET idx = #{index} WHERE id = #{id}")
    int updateIndex(@Param("id") Long id, @Param("index") Integer index);

    @Delete("DELETE FROM chapter WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
