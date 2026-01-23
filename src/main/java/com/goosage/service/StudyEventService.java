package com.goosage.service;

import org.springframework.stereotype.Service;
import com.goosage.dao.StudyEventDao;

@Service
public class StudyEventService {

    private final StudyEventDao studyEventDao;

    public StudyEventService(StudyEventDao studyEventDao) {
        this.studyEventDao = studyEventDao;
    }

    public void record(long userId, String type, Long knowledgeId) {
        // v1.0: "행동 1회 = 오늘 학습 인정"을 여기서 강제
        // eventType = type
        // refType = "KNOWLEDGE" (지금은 고정, 나중에 QUIZ_RESULT 등 확장)
        // payloadJson = null (나중에 확장)
        studyEventDao.recordEvent(userId, type, "KNOWLEDGE", knowledgeId, null);
    }
}
