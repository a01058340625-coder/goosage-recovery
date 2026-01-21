package com.goosage.academy.progress;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AcademyProgressService {

    private final AcademyProgressDao dao;

    public AcademyProgressService(AcademyProgressDao dao) {
        this.dao = dao;
    }

    // ✅ 8번: 진도 조회 (완료/미완료 + 퍼센트)
    @Transactional(readOnly = true)
    public ProgressSummary getProgress(long userId, long courseId) {

        List<AcademyProgressDao.ProgressItemRow> rows =
            dao.findProgressItems(userId, courseId);

        int total = rows.size();
        int done = (int) rows.stream()
                .filter(AcademyProgressDao.ProgressItemRow::done)
                .count();
        int percent = (total == 0) ? 0 : (done * 100 / total);

        List<ProgressSummary.ProgressItem> items = rows.stream()
            .map(r -> new ProgressSummary.ProgressItem(r.itemId(), r.knowledgeId(), r.done()))
            .toList();

        return new ProgressSummary(courseId, total, done, percent, items);
    }

    // ✅ 7번: 아이템 완료 처리 (진도 발생)
    @Transactional
    public void completeItem(long userId, long courseId, long itemId) {
        long workspaceId = requireWorkspaceId(courseId);
        requireMember(userId, workspaceId);

        dao.ensureEnrollment(userId, courseId);

        if (!dao.existsProgress(userId, courseId, itemId)) {
            try {
                dao.insertProgress(userId, courseId, itemId);
            } catch (DuplicateKeyException ignore) {
                // 동시성/중복 호출 대비
            }
        }

        dao.markDone(userId, courseId, itemId);
    }

    // -------- 권한/소속 체크 --------

    private long requireWorkspaceId(long courseId) {
        Long ws = dao.findWorkspaceIdByCourseId(courseId);
        if (ws == null || ws <= 0) throw new RuntimeException("NOT_FOUND");
        return ws;
    }

    private void requireMember(long userId, long workspaceId) {
        if (!dao.isMemberOfWorkspace(userId, workspaceId)) throw new RuntimeException("FORBIDDEN");
    }

    // ✅ DTO (한 파일 안에서 끝내기 버전)
    public record ProgressSummary(
            long courseId,
            int totalItems,
            int doneItems,
            int percent,
            List<ProgressItem> items
    ) {
        public record ProgressItem(long itemId, long knowledgeId, boolean done) {}
    }
}
