package com.noveloutline.common.entity;

import lombok.Data;

@Data
public class Volume {

    private Long id;
    private Long novelId;
    private Integer idx;
    private String title;
    private String summary;
}
