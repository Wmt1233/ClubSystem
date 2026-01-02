package com.example.club.repository;

import com.example.club.entity.ClubMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    List<ClubMember> findByClubId(Long clubId);
    Optional<ClubMember> findByClubIdAndStudentId(Long clubId, Long studentId);
    List<ClubMember> findByStudentId(Long studentId);
}