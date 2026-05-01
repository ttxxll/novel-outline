package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Faction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FactionMapper {

    @Insert("INSERT IGNORE INTO faction (novel_id, first_chapter_id, name, type, stance_toward_protagonist, description) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{type}, #{stanceTowardProtagonist}, #{description})")
    int insertIgnore(Faction record);

    @Select("SELECT * FROM faction WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<Faction> findByNovelId(Long novelId);

    @Delete("DELETE FROM faction WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
