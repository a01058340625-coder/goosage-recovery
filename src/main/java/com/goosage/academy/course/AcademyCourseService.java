package com.goosage.academy.course;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AcademyCourseService {

    private final AcademyCourseDao dao;

    public AcademyCourseService(AcademyCourseDao dao) {
        this.dao = dao;
    }

    @Transactional
    public CourseResponse createCourse(long userId, CourseCreateRequest req) {
        if (req == null) throw new RuntimeException("bad request");
        if (req.getWorkspaceId() <= 0) throw new RuntimeException("workspaceId 필수");
        if (!StringUtils.hasText(req.getTitle())) throw new RuntimeException("title 필수");

        long workspaceId = req.getWorkspaceId();
        if (!dao.isMemberOfWorkspace(userId, workspaceId)) throw new RuntimeException("FORBIDDEN");

        String title = req.getTitle().trim();
        String desc = (req.getDescription() == null) ? null : req.getDescription().trim();

        long courseId = dao.insertCourse(workspaceId, title, desc);

        // 생성 직후 items는 empty
        return new CourseResponse(courseId, workspaceId, title, desc, true, null, java.util.List.of());
    }

    @Transactional
    public CourseItemResponse addItem(long userId, long courseId, CourseItemAddRequest req) {
        if (courseId <= 0) throw new RuntimeException("courseId 필수");
        if (req == null) throw new RuntimeException("bad request");
        if (req.getKnowledgeId() <= 0) throw new RuntimeException("knowledgeId 필수");

        var course = dao.findCourse(courseId).orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (!dao.isMemberOfWorkspace(userId, course.workspaceId())) throw new RuntimeException("FORBIDDEN");

        int orderIndex = req.getOrderIndex();
        long itemId = dao.insertCourseItem(courseId, req.getKnowledgeId(), orderIndex);

        return new CourseItemResponse(itemId, req.getKnowledgeId(), orderIndex);
    }

    public CourseResponse getCourse(long userId, long courseId) {
        var course = dao.findCourse(courseId).orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (!dao.isMemberOfWorkspace(userId, course.workspaceId())) throw new RuntimeException("FORBIDDEN");

        var items = dao.findCourseItems(courseId);

        return new CourseResponse(
                course.id(), course.workspaceId(), course.title(), course.description(),
                course.active(), course.createdAt(), items
        );
    }
}
