package com.goosage.infra.dao;

import org.springframework.stereotype.Repository;

import com.goosage.domain.recovery.RecoveryStreakPort;

@Repository
public class RecoveryStreakDaoAdapter implements RecoveryStreakPort {

    private final RecoveryStreakDao studyStreakDao;

    public RecoveryStreakDaoAdapter(RecoveryStreakDao studyStreakDao) {
        this.studyStreakDao = studyStreakDao;
    }

    @Override
    public int countStreak(long userId) {
        return studyStreakDao.countStreak(userId);
    }
}
