package com.goosage.infra.dao;

import org.springframework.stereotype.Component;

import com.goosage.domain.study.StudyEventPort;

@Component
public class StudyEventDaoAdapter implements StudyEventPort {

    private final StudyEventDao studyEventDao;

    public StudyEventDaoAdapter(StudyEventDao studyEventDao) {
        this.studyEventDao = studyEventDao;
    }

    @Override
    public void recordEvent(long userId,
                            String type,
                            String targetType,
                            Long targetId,
                            String meta) {

        studyEventDao.recordEvent(userId, type, targetType, targetId, meta);
    }
}
