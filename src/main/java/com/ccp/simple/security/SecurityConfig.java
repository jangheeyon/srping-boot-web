package com.ccp.simple.security;

import com.ccp.simple.security.JwtAuthenticationFilter;
import com.ccp.simple.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 끄기
                .csrf(csrf -> csrf.disable())
                // H2 콘솔 접근 허용 위해 X-Frame-Options 비활성화
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authorizeHttpRequests(auth -> auth
                        // H2 콘솔 허용
                        .requestMatchers("/h2-console/**").permitAll()
                        // 정적 리소스(css, js, html, images 등) 허용
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // 루트 및 모든 하위 경로 허용 (HTML 포함)
                        .requestMatchers("/", "/**").permitAll()
                        // 로그인/토큰/회원가입 API 허용
                        .requestMatchers(
                                "/api/login",
                                "/api/refresh",
                                "/user/register"
                        ).permitAll()
                        // 관리자 전용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 유저, 관리자
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                        // 나머지 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())  //기본 로그인 폼 비활성화(직접 처리 중)
                // JWT 필터 등록
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebMvcConfigurer crosConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173")    //vue dev server
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }
}
