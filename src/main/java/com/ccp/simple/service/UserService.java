package com.ccp.simple.service;

import com.ccp.simple.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface UserService {
    List<User> getAllUsers();

    User getUserById(String userId);

    void updateUser(User user);

    void deleteUser(String userId);

    void insertUser(User user);

    String generateNextUserId();

    boolean validateUser(String userId, String userPassword);

    void updateRefreshToken(String userId, String refreshToken);
}
