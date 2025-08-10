package com.ccp.simple.dto;

import lombok.Data;

@Data
public class UpdateUserRequestDto {
    private String userId;
    private String userName;
    private String userPassword;
    private String role;
}
