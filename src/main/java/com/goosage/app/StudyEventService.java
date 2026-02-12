package com.goosage.app;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.goosage.domain.study.DailyLearningPort;
import com.goosage.domain.study.StudyEventPort;

@Service
public class StudyEventService {

    private final StudyEventPort studyEventPort;
    private final DailyLearningPort dailyLearningPort;

    public StudyEventService(StudyEventPort studyEventPort,
                             DailyLearningPort dailyLearningPort) {

        this.studyEventPort = studyEventPort;
        this.dailyLearningPort = dailyLearningPort;
    }

    @Transactional
    public void record(long userId, String type, Long knowledgeId) {

    	studyEventPort.recordEvent(userId, type, "KNOWLEDGE", knowledgeId, null);


        boolean isQuizSubmit = "QUIZ_SUBMIT".equals(type);
        boolean isWrongDone  = "WRONG_REVIEW_DONE".equals(type);

        dailyLearningPort.upsertToday(userId, isQuizSubmit, isWrongDone);
    }
}
