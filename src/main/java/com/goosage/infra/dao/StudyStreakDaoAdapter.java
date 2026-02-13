package com.goosage.infra.dao;

import org.springframework.stereotype.Repository;

import com.goosage.domain.study.StudyStreakPort;

@Repository
public class StudyStreakDaoAdapter implements StudyStreakPort {

    private final StudyStreakDao studyStreakDao;

    public StudyStreakDaoAdapter(StudyStreakDao studyStreakDao) {
        this.studyStreakDao = studyStreakDao;
    }

    @Override
    public int countStreak(long userId) {
        return studyStreakDao.countStreak(userId);
    }
}
