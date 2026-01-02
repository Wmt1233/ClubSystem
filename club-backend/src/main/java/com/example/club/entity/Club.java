package com.example.club.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
    private String description;
    private Long managerId; // 社长ID
}