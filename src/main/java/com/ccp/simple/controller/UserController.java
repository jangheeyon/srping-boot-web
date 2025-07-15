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

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable String userId) {
        return userService.getUserById(userId);
    }

    // 3. 회원 수정 (PUT /user/{id})
    @PutMapping("/{userId}")
    public void updateUser(@PathVariable String userId) {
       userService.updateUser(userId);
    }

    // 4. 회원 삭제 (DELETE /user/{id})
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
    }
}
