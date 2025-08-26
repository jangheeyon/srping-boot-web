package com.ccp.simple.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Keyword {
    private Long KeywordId;
    private String Keyword;
    private String userId;
    private LocalDateTime createDt;
}
