package com.goosage.infra.dao;

import org.springframework.stereotype.Component;
import com.goosage.domain.EventType;
import com.goosage.domain.study.StudyEventPort;

@Component
public class StudyEventDaoAdapter implements StudyEventPort {

    private final StudyEventDao studyEventDao;

    public StudyEventDaoAdapter(StudyEventDao studyEventDao) {
        this.studyEventDao = studyEventDao;
    }

    @Override
    public void recordEvent(long userId, EventType eventType, String refType, Long refId, String payloadJson) {
        studyEventDao.recordEvent(userId, eventType, refType, refId, payloadJson);
    }
}