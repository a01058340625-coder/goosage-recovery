package com.goosage.infra.dao;

import org.springframework.stereotype.Component;

import com.goosage.domain.study.DailyLearningPort;

@Component
public class DailyLearningDaoAdapter implements DailyLearningPort {

    private final DailyLearningDao dailyLearningDao;

    public DailyLearningDaoAdapter(DailyLearningDao dailyLearningDao) {
        this.dailyLearningDao = dailyLearningDao;
    }

    @Override
    public void upsertToday(long userId,
                            boolean isQuizSubmit,
                            boolean isWrongDone) {

        dailyLearningDao.upsertToday(
                userId,
                isQuizSubmit,
                isWrongDone
        );
    }
}
