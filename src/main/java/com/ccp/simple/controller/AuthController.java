package com.ccp.simple.controller;

import com.ccp.simple.domain.User;
import com.ccp.simple.dto.LoginRequestDto;
import com.ccp.simple.security.JwtTokenProvider;
import com.ccp.simple.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        //검증
        boolean valid = userService.validateUser(request.getUserId(), request.getUserPassword());
        if (valid) {
            String token = jwtTokenProvider.createToken(request.getUserId());
            response.setHeader("Authorization", "Bearer " + token);
            return "Login successful";
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return "Invalid credentials";
        }
    }

    @GetMapping("/me")
    public String currentUser(Authentication authentication) {
        if(authentication == null) {
            return "No Authentication found";
        }
        return "Currunt User ID : " + authentication.getPrincipal();
    }
}
