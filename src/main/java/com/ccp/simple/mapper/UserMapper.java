package com.ccp.simple.mapper;

import com.ccp.simple.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getAllUsers();

    User getUserById(String userId);

    void updateUser(String userId);

    void deleteUser(String userId);
}
