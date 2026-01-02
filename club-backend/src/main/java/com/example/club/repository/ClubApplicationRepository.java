package com.example.club.repository;

import com.example.club.entity.ClubApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClubApplicationRepository extends JpaRepository<ClubApplication, Long> {
    // 查询所有待审核的申请
    List<ClubApplication> findByStatus(String status);
}