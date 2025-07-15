package com.ccp.simple.domain;

import lombok.Data;

@Data
public class User {
    private String userId;
    private String userPassword;
    private String userName;
    private String createDt;
    private String updateDt;
}
