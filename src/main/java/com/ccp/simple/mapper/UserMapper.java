package com.ccp.simple.mapper;

import com.ccp.simple.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getUsers();
}
