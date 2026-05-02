package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Chapter;
import com.noveloutline.common.enums.ChapterStatus;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChapterMapper {

    @Select("SELECT * FROM chapter WHERE id = #{id}")
    Chapter findById(Long id);

    @Select("SELECT * FROM chapter WHERE novel_id = #{novelId} ORDER BY idx ASC")
    List<Chapter> findByNovelId(Long novelId);

    @Select("SELECT id, volume_id, novel_id, idx, title, status, word_count FROM chapter WHERE novel_id = #{novelId} ORDER BY idx ASC")
    List<Chapter> findByNovelIdLight(Long novelId);

    @Select("SELECT * FROM chapter WHERE volume_id = #{volumeId} ORDER BY idx ASC")
    List<Chapter> findByVolumeId(Long volumeId);

    @Select("SELECT * FROM chapter WHERE novel_id = #{novelId} AND status = #{status} ORDER BY idx ASC")
    List<Chapter> findByNovelIdAndStatus(@Param("novelId") Long novelId, @Param("status") ChapterStatus status);

    @Select("SELECT COUNT(*) FROM chapter WHERE novel_id = #{novelId}")
    long countByNovelId(Long novelId);

    @Select("SELECT COUNT(*) FROM chapter WHERE novel_id = #{novelId} AND status = #{status}")
    long countByNovelIdAndStatus(@Param("novelId") Long novelId, @Param("status") ChapterStatus status);

    @Insert("INSERT INTO chapter (volume_id, novel_id, idx, title, raw_content, analysis_result, status, word_count) " +
            "VALUES (#{volumeId}, #{novelId}, #{idx}, #{title}, #{rawContent}, #{analysisResult}, #{status}, #{wordCount})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Chapter chapter);

    @Update("UPDATE chapter SET analysis_result = #{analysisResult}, status = #{status}, title = #{title} WHERE id = #{id}")
    int update(Chapter chapter);

    @Update("UPDATE chapter SET idx = #{idx} WHERE id = #{id}")
    int updateIndex(@Param("id") Long id, @Param("idx") Integer idx);

    @Delete("DELETE FROM chapter WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
