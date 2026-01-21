package com.goosage.academy.progress;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcademyProgressService {

    private final AcademyProgressDao dao;

    public AcademyProgressService(AcademyProgressDao dao) {
        this.dao = dao;
    }

    @Service
    public class AcademyProgressService {

        private final AcademyProgressDao dao;

        public AcademyProgressService(AcademyProgressDao dao) {
            this.dao = dao;
        }

        // 🔹 7번용 (오늘은 안 씀 → 잠시 주석)
        /*
        @Transactional
        public void completeItem(long userId, long courseId, long itemId) {
            // TODO: 나중에
        }
        */

        // 🔹 8번: 진도 조회
        public ProgressSummary getProgress(long userId, long courseId) {
            var items = dao.findProgressItems(userId, courseId);

            int total = items.size();
            long done = items.stream().filter(AcademyProgressDao.ProgressItemRow::done).count();
            int percent = total == 0 ? 0 : (int) (done * 100 / total);

            return new ProgressSummary(courseId, total, (int) done, percent, items);
        }
    }


    @Transactional
    public void completeItem(long userId, long courseId, long itemId) {
        long workspaceId = requireWorkspaceId(courseId);
        requireMember(userId, workspaceId);

        dao.ensureEnrollment(userId, courseId);

        if (!dao.existsProgress(userId, courseId, itemId)) {
            try { dao.insertProgress(userId, courseId, itemId); }
            catch (org.springframework.dao.DuplicateKeyException ignore) {}
        }

        dao.markDone(userId, courseId, itemId);
    }



    public ProgressSummary getProgress(long userId, long courseId) {
        long workspaceId = requireWorkspaceId(courseId);
        requireMember(userId, workspaceId);

        int total = dao.countTotalItems(courseId);
        int done = dao.countDoneItems(userId, courseId);
        double percent = (total == 0) ? 0.0 : (done * 100.0 / total);

        return new ProgressSummary(courseId, total, done, percent);
    }

    private long requireWorkspaceId(long courseId) {
        Long ws = dao.findWorkspaceIdByCourseId(courseId);
        if (ws == null || ws <= 0) throw new RuntimeException("NOT_FOUND");
        return ws;
    }

    private void requireMember(long userId, long workspaceId) {
        if (!dao.isMemberOfWorkspace(userId, workspaceId)) throw new RuntimeException("FORBIDDEN");
    }

    public record ProgressSummary(long courseId, int totalItems, int doneItems, double percent) {}
}
