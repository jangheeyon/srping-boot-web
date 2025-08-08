package com.ccp.simple.config;

import com.ccp.simple.security.JwtAuthenticationFilter;
import com.ccp.simple.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스와 페이지는 모두 허용
                        .requestMatchers(
                                "/h2-console/**",
                                "/",
                                "/index",
                                "/index.html",
                                "/login.html",
                                "/userList.html",
                                "/js/**",   // js 폴더 등 프론트 정적 파일 경로가 있다면 추가
                                "/css/**",  // css 경로도 필요하면 추가
                                "/api/login",
                                "/api/refresh",
                                "/user/register"
                        ).permitAll()
                        // API 권한 설정
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        // 그 외는 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}
