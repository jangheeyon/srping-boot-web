package com.ccp.simple.service.impl;

import com.ccp.simple.domain.User;
import com.ccp.simple.mapper.UserMapper;
import com.ccp.simple.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return userMapper.getUserById(userId);
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
        userMapper.insertUser(user);
    }
}
