package com.goosage.infra.dao;

import org.springframework.stereotype.Component;
import com.goosage.domain.EventType;
import com.goosage.domain.study.StudyDebugPort;

@Component
public class StudyDebugDaoAdapter implements StudyDebugPort {

    private final StudyEventDao studyEventDao;

    public StudyDebugDaoAdapter(StudyEventDao studyEventDao) {
        this.studyEventDao = studyEventDao;
    }

    @Override
    public void recordPing(Long userId) {
    	studyEventDao.recordEvent(userId, EventType.PING, null, null, null);
    }
}
