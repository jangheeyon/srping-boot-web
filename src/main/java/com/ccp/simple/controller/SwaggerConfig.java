package com.ccp.simple.controller;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("회원 관리 API")
                        .description("Spring Boot + MyBatis 기반 회원관리 시스템 API 문서")
                        .version("v1.0"));
    }
}
