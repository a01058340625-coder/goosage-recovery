package com.goosage.academy.course;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.auth.SessionUtil;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/academy/courses")
public class AcademyCourseController {

    private final AcademyCourseService service;

    public AcademyCourseController(AcademyCourseService service) {
        this.service = service;
    }

    private long requireUserId(HttpSession session) {
        Object v = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (v == null) throw new RuntimeException("UNAUTHORIZED");
        if (v instanceof Long l) return l;
        if (v instanceof Integer i) return i.longValue();
        throw new RuntimeException("UNAUTHORIZED");
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody CourseCreateRequest req, HttpSession session) {
        long userId = SessionUtil.requireUserId(session);
        var r = service.createCourse(userId, req);
        return Map.of("success", true, "message", "CREATED", "data", r);
    }

    @PostMapping("/{courseId}/items")
    public Map<String, Object> addItem(@PathVariable long courseId,
                                       @RequestBody CourseItemAddRequest req,
                                       HttpSession session) {
        long userId = requireUserId(session);
        var r = service.addItem(userId, courseId, req);
        return Map.of("success", true, "message", "CREATED", "data", r);
    }

    @GetMapping("/{courseId}")
    public Map<String, Object> get(@PathVariable long courseId, HttpSession session) {
        long userId = requireUserId(session);
        var r = service.getCourse(userId, courseId);
        return Map.of("success", true, "message", "OK", "data", r);
    }
}
