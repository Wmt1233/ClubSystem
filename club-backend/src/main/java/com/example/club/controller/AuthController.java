package com.example.club.controller;

import com.example.club.entity.User;
import com.example.club.repository.UserRepository;
import com.example.club.util.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    // ... 原有的 login 方法保持不变 ...
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // 1. 查询用户 (不再直接抛异常)
        Optional<User> userOp = userRepository.findByUsername(req.getUsername());

        // 【场景一：账号未注册】
        if (userOp.isEmpty()) {
            // 返回 401 状态码，并告诉前端具体原因
            return ResponseEntity.status(401).body(Map.of("message", "该账号未注册，请先注册"));
        }

        User user = userOp.get();

        // 【场景二：密码错误】
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "密码错误，请重试"));
        }

        // 【场景三：账号被封禁】
        if (user.isBanned()) {
            // 返回 403 禁止访问状态码
            return ResponseEntity.status(401).body(Map.of("message", "您的账号已被封禁，请联系管理员"));
        }

        // 4. 登录成功：生成 Token
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        Map<String, Object> res = new HashMap<>();
        res.put("token", token);
        res.put("role", user.getRole());
        res.put("username", user.getUsername());
        res.put("userId", user.getId());
        res.put("message", "Login successful");

        return ResponseEntity.ok(res);
    }
    // --- 【新增：注册接口】 ---
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {
        // 1. 检查用户名是否已存在
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在，请更换");
        }

        // 2. 创建新用户
        User user = new User();
        user.setUsername(req.getUsername());
        // 必须加密密码！
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        // 默认注册的都是学生
        user.setRole("student");
        user.setBanned(false);

        userRepository.save(user);
        return "注册成功，请去登录";
    }

    // 定义请求参数结构
    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }

    // 【新增】注册请求结构
    @Data
    static class RegisterRequest {
        private String username;
        private String password;
    }
}