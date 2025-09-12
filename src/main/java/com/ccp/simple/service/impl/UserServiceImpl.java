package com.ccp.simple.service.impl;

import com.ccp.simple.domain.User;
import com.ccp.simple.exception.UserNotFoundException;
import com.ccp.simple.mapper.UserMapper;
import com.ccp.simple.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public User getUserById(String userId) {
        return Optional.ofNullable(userMapper.getUserById(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    @Override
    public void updateUser(User user) {
         userMapper.updateUser(user);
    }

    @Override
    public void deleteUser(String userId) {
        userMapper.deleteUser(userId);
    }

    @Override
    public void insertUser(User user) {
        String newId = generateNextUserId();
        user.setId(newId);
        userMapper.insertUser(user);
    }

    @Override
    public String generateNextUserId() {
        String maxId = userMapper.getMaxUserId();
        if (maxId == null) {
            return "A001";
        }
        int num = Integer.parseInt(maxId.substring(1));
        num++;
        return String.format("A%03d", num);
    }

    @Override
    public boolean validateUser(String userId, String userPassword) {
        User user = userMapper.getUserById(userId);
        return user != null && user.getUserPassword().equals(userPassword);
    }

    @Override
    public void updateRefreshToken(String userId, String refreshToken) {
        User user = new User();
        user.setUserId(userId);
        user.setRefreshToken(refreshToken);
        userMapper.updateUser(user);
    }
}
