package com.ccp.simple.controller;

import com.ccp.simple.aop.LogActivity;
import com.ccp.simple.domain.Role;
import com.ccp.simple.domain.User;
import com.ccp.simple.dto.LoginRequestDto;
import com.ccp.simple.security.JwtTokenProvider;
import com.ccp.simple.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @LogActivity("사용자 로그인")
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        //검증
        boolean valid = userService.validateUser(request.getUserId(), request.getUserPassword());
        if (!valid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        User user = userService.getUserById(request.getUserId());
        Role role = user.getRole();
        String accessToken = jwtTokenProvider.createToken(request.getUserId(), role);
        String refreshToken = jwtTokenProvider.createRefreshToken(request.getUserId());

        userService.updateRefreshToken(request.getUserId(), refreshToken);

        Map<String, String> token = new HashMap<>();
        token.put("accessToken", accessToken);
        token.put("refreshToken", refreshToken);
        return token;
    }

    @LogActivity("토큰 재발급")
    @PostMapping("/refresh")
    public Map<String, String> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String userId = jwtTokenProvider.getUserId(refreshToken);
        User user = userService.getUserById(userId);
        String savedRefreshToken = user.getRefreshToken();
        if (!refreshToken.equals(savedRefreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token mismatch");
        }

        Role role = user.getRole();
        String newAccessToken = jwtTokenProvider.createToken(userId, role);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        return tokens;
    }

    @LogActivity("사용자 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        if (authentication == null) {
            Map<String, String> body = new HashMap<>();
            body.put("message", "이미 로그아웃 상태입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
        }
        String userId = authentication.getName();
        userService.updateRefreshToken(userId, null);

        Map<String, String> body = new HashMap<>();
        body.put("message", "로그아웃 되었습니다.");
        return ResponseEntity.ok(body);
    }
}
