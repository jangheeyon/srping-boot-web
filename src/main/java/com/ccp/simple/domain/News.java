package com.ccp.simple.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class News {
    private Long newsId;
    private String title;
    private String link;
    private String description;
    private LocalDateTime pubDt;
    private LocalDateTime createDt;
}
