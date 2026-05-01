package com.noveloutline.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Character {
    private Long id;
    private Long novelId;
    private Long firstChapterId;
    private String name;
    private String role;
    private String relationshipToProtagonist;
    private Date createdAt;
}
