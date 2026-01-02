package com.example.club.controller;

import com.example.club.entity.Club;
import com.example.club.entity.ClubApplication;
import com.example.club.entity.ClubMember;
import com.example.club.entity.User;
import com.example.club.repository.ClubApplicationRepository;
import com.example.club.repository.ClubMemberRepository;
import com.example.club.repository.ClubRepository;
import com.example.club.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired private ClubRepository clubRepository;
    @Autowired private ClubApplicationRepository applicationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClubMemberRepository memberRepository; // 【新增注入】

    @GetMapping
    public List<Club> getAllClubs() {
        return clubRepository.findAll();
    }

    // 学生提交建社申请
    @PostMapping("/apply")
    public String applyClub(@RequestBody ClubApplication app) {
        User user = userRepository.findById(app.getApplicantId()).orElseThrow();
        app.setApplicantName(user.getUsername());
        app.setStatus("PENDING");
        applicationRepository.save(app);
        return "申请已提交，请等待管理员审核";
    }

    // --- 【新增：成员管理接口】 ---

    // 1. 学生申请加入社团
    @PostMapping("/{clubId}/join")
    public String joinClub(@PathVariable Long clubId, @RequestBody User student) {
        // A. 先检查社团是否存在
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("社团不存在"));

        // 【新增逻辑】B. 如果申请人就是该社团的社长，直接拦截
        // 注意：比较 Long 类型建议用 equals
        if (club.getManagerId().equals(student.getId())) {
            return "您是该社团社长，无需加入";
        }

        // C. 检查是否重复加入
        if (memberRepository.findByClubIdAndStudentId(clubId, student.getId()).isPresent()) {
            return "您已申请或加入过该社团";
        }

        // D. 正常入社逻辑
        ClubMember member = new ClubMember();
        member.setClubId(clubId);
        member.setStudentId(student.getId());
        member.setStudentName(student.getUsername());
        member.setStatus("PENDING");
        memberRepository.save(member);
        return "入社申请已提交";
    }

    // 2. 社长获取自己社团的成员列表
    @GetMapping("/my-members")
    public List<ClubMember> getMyMembers(@RequestParam Long managerId) {
        Club myClub = clubRepository.findAll().stream()
                .filter(c -> c.getManagerId().equals(managerId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("您还没有管理任何社团"));

        return memberRepository.findByClubId(myClub.getId());
    }

    // 3. 社长审批成员
    @PutMapping("/members/{memberId}/approve")
    public String approveMember(@PathVariable Long memberId) {
        ClubMember member = memberRepository.findById(memberId).orElseThrow();
        member.setStatus("APPROVED");
        memberRepository.save(member);
        return "已批准入社";
    }

    @GetMapping("/my-managed")
    public List<Club> getMyManagedClubs(@RequestParam Long managerId) {
        // 筛选出 managerId 等于传入值的社团
        return clubRepository.findAll().stream()
                .filter(c -> c.getManagerId().equals(managerId))
                .toList();
    }

    @GetMapping("/my-membership-status")
    public Map<Long, String> getMyMembershipStatus(@RequestParam Long userId) {
        // 1. 查找该学生所有的入社记录
        List<ClubMember> members = memberRepository.findByStudentId(userId);

        // 2. 转换成 Map 格式: Key=ClubId, Value=Status (PENDING/APPROVED)
        // 例如: {1: "APPROVED", 2: "PENDING"}
        return members.stream()
                .collect(Collectors.toMap(ClubMember::getClubId, ClubMember::getStatus));
    }



}