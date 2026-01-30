package com.goosage.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goosage.dao.StudyEventDao;
import com.goosage.dao.study.DailyLearningDao;

@Service
public class StudyEventService {

    private final StudyEventDao studyEventDao;
    private final DailyLearningDao dailyLearningDao;

    public StudyEventService(StudyEventDao studyEventDao, DailyLearningDao dailyLearningDao) {
        this.studyEventDao = studyEventDao;
        this.dailyLearningDao = dailyLearningDao;
    }

    @Transactional
    public void record(long userId, String type, Long knowledgeId) {
        // v1.0: 행동 1회 = 오늘 학습 인정 (이벤트 원장 기록)
        studyEventDao.recordEvent(userId, type, "KNOWLEDGE", knowledgeId, null);


        // ✅ A안: 원장 저장 직후, 오늘 롤업 즉시 갱신
        boolean isQuizSubmit = "QUIZ_SUBMIT".equals(type);
        boolean isWrongDone  = "WRONG_REVIEW_DONE".equals(type); // 네 프로젝트 실제 타입명에 맞춰
        dailyLearningDao.upsertToday(userId, isQuizSubmit, isWrongDone);
    }
}
