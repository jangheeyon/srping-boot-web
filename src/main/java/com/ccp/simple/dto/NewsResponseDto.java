package com.ccp.simple.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NewsResponseDto {
    private Long newsId;
    private String title;
    private String link;
    private String description;
    private LocalDateTime pubDt;
    private String keywords;
    private boolean visible;
}
