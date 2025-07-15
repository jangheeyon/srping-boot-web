package com.ccp.simple.controller;

import com.ccp.simple.domain.User;
import com.ccp.simple.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    //조회
    @GetMapping()
    public List<User> getUsers() {
        List<User> users = userService.getUsers();
        return users;
    }
}
