package com.example.club.controller;

import com.example.club.entity.Club;
import com.example.club.entity.ClubApplication;
import com.example.club.entity.User;
import com.example.club.repository.ClubApplicationRepository;
import com.example.club.repository.ClubRepository;
import com.example.club.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private ClubApplicationRepository applicationRepository;
    @Autowired private ClubRepository clubRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/users/{id}/ban")
    public String toggleBan(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setBanned(!user.isBanned());
        userRepository.save(user);
        return user.isBanned() ? "用户已封禁" : "用户已解封";
    }

    // --- 新增：获取待审核列表 ---
    @GetMapping("/applications")
    public List<ClubApplication> getPendingApplications() {
        return applicationRepository.findByStatus("PENDING");
    }

    // --- 新增：审批通过 ---
    @PostMapping("/applications/{id}/approve")
    public String approveApplication(@PathVariable Long id) {
        ClubApplication app = applicationRepository.findById(id).orElseThrow();

        // 1. 修改申请状态
        app.setStatus("APPROVED");
        applicationRepository.save(app);

        // 2. 自动创建新社团
        Club newClub = new Club();
        newClub.setName(app.getName());
        newClub.setCategory(app.getCategory());
        newClub.setDescription(app.getDescription());
        newClub.setManagerId(app.getApplicantId());
        clubRepository.save(newClub);

        // 3. 将申请人升级为社长 (Manager)
        User applicant = userRepository.findById(app.getApplicantId()).orElseThrow();
        applicant.setRole("manager");
        userRepository.save(applicant);

        return "审批通过，社团已创建，用户已升级为社长";
    }

    // --- 新增：驳回申请 ---
    @PostMapping("/applications/{id}/reject")
    public String rejectApplication(@PathVariable Long id) {
        ClubApplication app = applicationRepository.findById(id).orElseThrow();
        app.setStatus("REJECTED");
        applicationRepository.save(app);
        return "申请已驳回";
    }
}