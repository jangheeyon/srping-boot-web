package com.ccp.simple.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RegisterKeywordRequestDto {
    private String Keyword;
    private String userId;
}
