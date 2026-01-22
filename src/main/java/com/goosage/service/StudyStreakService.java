package com.goosage.service;

import org.springframework.stereotype.Service;

import com.goosage.dao.study.StudyStreakDao;

@Service
public class StudyStreakService {

    private final StudyStreakDao studyStreakDao;

    public StudyStreakService(StudyStreakDao studyStreakDao) {
        this.studyStreakDao = studyStreakDao;
    }

    public int getStreak(long userId) {
        return studyStreakDao.countStreak(userId);
    }
}
