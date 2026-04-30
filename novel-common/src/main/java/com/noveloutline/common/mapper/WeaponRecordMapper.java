package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.WeaponRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WeaponRecordMapper {

    @Insert("INSERT IGNORE INTO weapon_record (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(WeaponRecord record);

    @Select("SELECT * FROM weapon_record WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<WeaponRecord> findByNovelId(Long novelId);

    @Delete("DELETE FROM weapon_record WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
