package com.ccp.simple.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestDto {
    private String userId;
    private String userPassword;
    private String role;
}