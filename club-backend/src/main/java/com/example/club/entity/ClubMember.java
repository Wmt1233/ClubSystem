package com.example.club.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clubId;
    private Long studentId;
    private String studentName; // 冗余存储方便显示

    // PENDING (申请中), APPROVED (正式成员)
    private String status = "PENDING";
}