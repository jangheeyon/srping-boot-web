package com.ccp.simple.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String userId;
    private String userName;
    private String userPassword;
    private String role;
}
