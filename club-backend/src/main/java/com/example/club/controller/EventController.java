package com.example.club.controller;

import com.example.club.entity.Club;
import com.example.club.entity.Event;
import com.example.club.entity.User;
import com.example.club.repository.ClubRepository;
import com.example.club.repository.EventRepository;
import com.example.club.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired private EventRepository eventRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClubRepository clubRepository; // 【新增注入】

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping
    public String createEvent(@RequestBody Event event) {
        event.setTime(LocalDateTime.now());
        eventRepository.save(event);
        return "活动发布成功";
    }

    @PostMapping("/{id}/join")
    public String joinEvent(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Event event = eventRepository.findById(id).orElseThrow();
        if (event.getParticipantIds().contains(userId)) {
            return "您已报名过该活动";
        }
        event.getParticipantIds().add(userId);
        eventRepository.save(event);
        return "报名成功";
    }

    // --- 【新增：删除活动接口】 ---
    @DeleteMapping("/{id}")
    public String deleteEvent(@PathVariable Long id, @RequestParam Long userId) {
        // 1. 获取活动信息
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("活动不存在"));

        // 2. 获取当前操作用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 3. 权限判断
        if ("admin".equals(user.getRole())) {
            // A. 管理员：直接删除
            eventRepository.delete(event);
            return "活动已删除 (管理员操作)";
        } else if ("manager".equals(user.getRole())) {
            // B. 社长：检查是否是本社团的活动
            // 通过活动的 clubId 找到对应的社团
            Club club = clubRepository.findById(event.getClubId())
                    .orElseThrow(() -> new RuntimeException("关联社团不存在"));

            // 比较社团的 managerId 和当前 userId
            if (club.getManagerId().equals(userId)) {
                eventRepository.delete(event);
                return "活动已删除";
            } else {
                throw new RuntimeException("您无权删除其他社团的活动");
            }
        } else {
            // C. 学生或其他：无权操作
            throw new RuntimeException("权限不足");
        }
    }
}