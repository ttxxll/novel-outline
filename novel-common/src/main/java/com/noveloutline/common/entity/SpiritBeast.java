package com.noveloutline.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SpiritBeast {
    private Long id;
    private Long novelId;
    private Long firstChapterId;
    private String name;
    private String level;
    private String significance;
    private Date createdAt;
}
