package com.goosage.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goosage.domain.study.StudyEventPort;

@Service
public class StudyEventService {

    private final StudyEventPort studyEventPort;

    public StudyEventService(StudyEventPort studyEventPort) {
        this.studyEventPort = studyEventPort;
    }

    @Transactional
    public void record(long userId, String type, Long knowledgeId) {

        // knowledgeId가 없으면 refType도 null로 두는 게 더 자연스러움(선택)
        String refType = (knowledgeId == null) ? null : "KNOWLEDGE";

        studyEventPort.recordEvent(userId, type, refType, knowledgeId, null);

        // ✅ daily_learning 집계는 StudyEventDao.recordEvent() 내부 upsertDaily()에서만 처리한다.
        //    (중복 집계 방지: service 레벨에서 upsert 금지)
    }
}