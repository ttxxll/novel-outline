package com.noveloutline.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Weapon {
    private Long id;
    private Long novelId;
    private Long firstChapterId;
    private String name;
    private String grade;
    private String significance;
    private Date createdAt;
}
