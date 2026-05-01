package com.noveloutline.common.mapper;

import com.noveloutline.common.entity.Weapon;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface WeaponMapper {

    @Insert("INSERT IGNORE INTO weapon (novel_id, first_chapter_id, name, grade, significance) " +
            "VALUES (#{novelId}, #{firstChapterId}, #{name}, #{grade}, #{significance})")
    int insertIgnore(Weapon record);

    @Select("SELECT * FROM weapon WHERE novel_id = #{novelId} ORDER BY id ASC")
    List<Weapon> findByNovelId(Long novelId);

    @Delete("DELETE FROM weapon WHERE novel_id = #{novelId}")
    int deleteByNovelId(Long novelId);
}
