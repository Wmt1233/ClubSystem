package com.example.club.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 开启跨域 (CORS) 并 关闭 CSRF (非常重要，否则 POST 请求会报 403)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // 2. 配置路径权限
                .authorizeHttpRequests(auth -> auth
                        // 允许未登录访问的接口：登录、注册、静态资源
                        .requestMatchers("/api/login", "/api/register").permitAll()
                        // 允许访问 H2 数据库控制台 (如果开启了的话)
                        .requestMatchers("/h2-console/**").permitAll()
                        // 其他所有 /api/** 请求都需要 Token 认证 (其实我们没做 Filter, 这里主要为了防错)
                        // 由于我们是前后端分离，主要靠 JWT Filter 拦截，这里可以配置为 permitAll
                        // 或者 authenticated() 配合你自己的拦截器。
                        // 为了简单起见，先把所有请求放行，依靠我们在 Controller 里的逻辑或 JWT 拦截器
                        .anyRequest().permitAll()
                )

                // 3. 允许 H2 控制台的 frame 加载
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    // 配置密码加密器 (注册和登录时用到)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 配置跨域 (允许前端 5173 访问 后端 8080)
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);

        // 【删除旧的】 config.addAllowedOrigin("http://localhost:5173");

        // 【新增这行】 允许所有来源模式 (解决 127.0.0.1 vs localhost 以及端口不同的问题)
        config.addAllowedOriginPattern("*");

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}