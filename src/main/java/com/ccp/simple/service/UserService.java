package com.ccp.simple.service;

import com.ccp.simple.domain.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(String userId);

    void updateUser(String userId);

    void deleteUser(String userId);
}
