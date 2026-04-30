package com.noveloutline.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FactionRecord {
    private Long id;
    private Long novelId;
    private Long firstChapterId;
    private String name;
    private String type;
    private String stanceTowardProtagonist;
    private String description;
    private Date createdAt;
}
