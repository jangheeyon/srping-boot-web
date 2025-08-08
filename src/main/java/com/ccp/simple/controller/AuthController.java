package com.ccp.simple.controller;

import com.ccp.simple.domain.User;
import com.ccp.simple.dto.LoginRequestDto;
import com.ccp.simple.security.JwtTokenProvider;
import com.ccp.simple.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        //검증
        boolean valid = userService.validateUser(request.getUserId(), request.getUserPassword());
        if(!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid credentials"));
        }
        User user = userService.getUserById(request.getUserId());
        String role = user.getRole();
        String token = jwtTokenProvider.createToken(request.getUserId(), role);
        response.setHeader("Authorization", "Bearer " + token);
        return ResponseEntity.ok(Map.of("message", "Login successful", "token", token));
    }

    @GetMapping("/me")
    public String currentUser(Authentication authentication) {
        if(authentication == null) {
            return "No Authentication found";
        }
        return "Currunt User ID : " + authentication.getPrincipal();
    }

    @GetMapping("/admin/test")
    public String adminOnly() {
        return "관리자만 접근 가능";
    }

    @GetMapping("/user/test")
    public String userAll() {
        return "유저 접근 가능";
    }
}
