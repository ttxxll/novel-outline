package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Volume;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VolumeMapper {

    @Select("SELECT * FROM volume WHERE id = #{id}")
    Volume findById(Long id);

    @Select("SELECT * FROM volume WHERE novel_id = #{novelId} ORDER BY idx ASC")
    List<Volume> findByNovelId(Long novelId);

    @Insert("INSERT INTO volume (novel_id, idx, title, summary) VALUES (#{novelId}, #{idx}, #{title}, #{summary})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Volume volume);

    @Update("UPDATE volume SET title = #{title}, summary = #{summary} WHERE id = #{id}")
    int update(Volume volume);

    @Delete("DELETE FROM volume WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
