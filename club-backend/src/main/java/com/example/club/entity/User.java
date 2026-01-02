package com.example.club.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users") // 建议指定表名，有些数据库 user 是关键字
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;

    // 【关键修改】：直接在这里赋值 = false
    private boolean banned = false;
    // 注意：尽量使用 boolean (小写) 而不是 Boolean (大写)，除非你需要 null 状态
}