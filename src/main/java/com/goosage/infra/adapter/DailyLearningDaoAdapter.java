package com.goosage.infra.adapter;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.DailyLearningPort;
import com.goosage.infra.dao.DailyLearningDao;

@Component
public class DailyLearningDaoAdapter implements DailyLearningPort {

    private final DailyLearningDao dailyLearningDao;

    public DailyLearningDaoAdapter(DailyLearningDao dailyLearningDao) {
        this.dailyLearningDao = dailyLearningDao;
    }

    @Override
    public void upsertToday(long userId, boolean isQuizSubmit, boolean isReviewWrong) {
        dailyLearningDao.upsertToday(userId, isQuizSubmit, isReviewWrong);
    }
}