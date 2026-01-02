package com.example.club.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ClubApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 拟定社团名称
    private String category;    // 分类
    private String description; // 申请理由/简介

    private Long applicantId;   // 申请人ID
    private String applicantName; // 申请人名字 (方便显示)

    // 状态: PENDING(待审核), APPROVED(已通过), REJECTED(已拒绝)
    private String status = "PENDING";
}