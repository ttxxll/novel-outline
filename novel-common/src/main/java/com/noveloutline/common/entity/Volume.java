package com.noveloutline.common.entity;

import lombok.Data;

@Data
public class Volume {

    private Long id;
    private Long novelId;
    private Integer index;
    private String title;
    private String summary;
}
