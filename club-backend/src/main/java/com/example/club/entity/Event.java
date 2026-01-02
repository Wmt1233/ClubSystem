package com.example.club.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;       // 活动标题
    private String content;     // 活动内容
    private String location;    // 地点
    private LocalDateTime time; // 时间
    private Long clubId;        // 所属社团ID
    private String clubName;    // 社团名称（冗余存储方便查询）

    @ElementCollection
    private List<Long> participantIds = new ArrayList<>(); // 存储报名学生的ID
}