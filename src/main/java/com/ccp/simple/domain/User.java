package com.ccp.simple.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {
    private String userId;
    private String userPassword;
    private String userName;
    private String createDt;
    private String updateDt;
    private String role;
    private String refreshToken;
}
