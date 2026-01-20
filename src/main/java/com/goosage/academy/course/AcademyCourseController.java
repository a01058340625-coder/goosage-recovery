package com.goosage.academy.course;

import com.goosage.auth.SessionConst;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
        long userId = requireUserId(session);
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
