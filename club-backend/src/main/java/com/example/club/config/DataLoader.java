package com.example.club.config;

import com.example.club.entity.Club;
import com.example.club.entity.User;
import com.example.club.repository.ClubRepository;
import com.example.club.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, ClubRepository clubRepo, PasswordEncoder encoder) {
        return args -> {
            // 1. 安全地初始化用户（防止重复！）
            User admin = createOrGetUser(userRepo, "admin", encoder.encode("123"), "admin");
            User manager = createOrGetUser(userRepo, "manager", encoder.encode("123"), "manager");
            User student = createOrGetUser(userRepo, "student", encoder.encode("123"), "student");

            // 2. 初始化社团（仅当没有任何社团时才创建）
            if (clubRepo.count() == 0) {
                Club c1 = new Club();
                c1.setName("Java 编程社");
                c1.setCategory("科技");
                c1.setDescription("Spring Boot 学习");
                c1.setManagerId(manager.getId()); // 使用刚才获取/创建的 manager ID
                clubRepo.save(c1);

                Club c2 = new Club();
                c2.setName("篮球社");
                c2.setCategory("体育");
                c2.setDescription("无兄弟不篮球");
                c2.setManagerId(999L);
                clubRepo.save(c2);
            }
        };
    }

    // --- 核心修复逻辑 ---
    private User createOrGetUser(UserRepository repo, String username, String password, String role) {
        // 先去查一下，如果找到了就直接返回现有的
        Optional<User> existing = repo.findByUsername(username);
        if (existing.isPresent()) {
            System.out.println("用户 " + username + " 已存在，跳过创建。");
            return existing.get();
        }

        // 如果没找到，才新建
        User u = new User();
        u.setUsername(username);
        u.setPassword(password);
        u.setRole(role);
        u.setBanned(false);
        System.out.println("初始化创建用户: " + username);
        return repo.save(u);
    }
}