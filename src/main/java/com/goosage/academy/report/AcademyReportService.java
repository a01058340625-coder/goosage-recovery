package com.goosage.academy.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goosage.academy.course.AcademyCourseDao;
import com.goosage.academy.progress.AcademyProgressDao;

import java.util.List;
import java.util.Optional;


@Service
public class AcademyReportService {

    private final AcademyReportDao reportDao;
    private final AcademyProgressDao progressDao; // workspaceId + member check 재사용
    private final AcademyCourseDao courseDao;     // (원하면 course 존재 체크용)

    public AcademyReportService(AcademyReportDao reportDao, AcademyProgressDao progressDao, AcademyCourseDao courseDao) {
        this.reportDao = reportDao;
        this.progressDao = progressDao;
        this.courseDao = courseDao;
    }

    @Transactional(readOnly = true)
    public CourseReportResponse getCourseReport(long userId, long courseId) {
        // 1) 코스 존재 확인(선택이지만 추천)
        courseDao.findCourse(courseId).orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        // 2) 권한(현재 정책: workspace 멤버면 OK)
        long workspaceId = requireWorkspaceId(courseId);
        requireMember(userId, workspaceId);

        // 3) 집계
        int totalItems = reportDao.countTotalItems(courseId);
        int enrolledUsers = reportDao.countEnrolledUsers(courseId);
        int activeUsers = reportDao.countActiveUsers(courseId);
        int completedUsers = reportDao.countCompletedUsers(courseId, totalItems);
        int avgPercent = reportDao.avgCompletionPercent(courseId, totalItems);

        var topWrong = reportDao.topWrongKnowledges(courseId, 5);

        return new CourseReportResponse(
            courseId,
            totalItems,
            enrolledUsers,
            activeUsers,
            completedUsers,
            avgPercent,
            reportDao.lastActivityAt(courseId),
            reportDao.topStuckItems(courseId, 5),
            topWrong
        );

    }

    private long requireWorkspaceId(long courseId) {
        Long ws = progressDao.findWorkspaceIdByCourseId(courseId);
        if (ws == null || ws <= 0) throw new RuntimeException("NOT_FOUND");
        return ws;
    }

    private void requireMember(long userId, long workspaceId) {
        if (!progressDao.isMemberOfWorkspace(userId, workspaceId)) throw new RuntimeException("FORBIDDEN");
    }
    
    @Transactional(readOnly = true)
    public List<CourseUserReportResponse> getCourseUserReports(long userId, long courseId) {
        // 코스 존재 확인(선택)
        courseDao.findCourse(courseId).orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        // 권한 체크 (workspace 멤버면 OK)
        long workspaceId = requireWorkspaceId(courseId);
        requireMember(userId, workspaceId);

        // 유저별 리스트
        return reportDao.findUserReports(courseId);
    }

}
