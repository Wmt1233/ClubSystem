package com.example.club.repository;
import com.example.club.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByClubId(Long clubId); // 查询某社团的活动
}