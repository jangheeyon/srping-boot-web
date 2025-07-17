package com.ccp.simple.controller;

import com.ccp.simple.domain.User;
import com.ccp.simple.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    //조회
    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    //개별 조회
    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    //회원 수정
    @PutMapping("/{userId}")
    public void updateUser(@RequestBody User user) {
       userService.updateUser(user);
    }

    //회원 삭제
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }

    //회원 등록
    @PostMapping()
    public void insertUser(@RequestBody User user) {
        String newId = userService.generateNextUserId();
        user.setUserId(newId);
        userService.insertUser(user);
    }

}
