package com.goosage.infra.dao;

import org.springframework.stereotype.Component;

import com.goosage.domain.stats.StatsPort;
import com.goosage.dto.StatsOverviewResponse;

@Component
public class StatsDaoAdapter implements StatsPort {

    private final StatsDao statsDao;

    public StatsDaoAdapter(StatsDao statsDao) {
        this.statsDao = statsDao;
    }

    @Override
    public StatsOverviewResponse overview(long userId, int days) {
        return new StatsOverviewResponse(
                userId,
                statsDao.attemptsInDays(userId, days),
                statsDao.avgScoreInDays(userId, days),
                statsDao.attemptsToday(userId),
                statsDao.avgScoreToday(userId),
                statsDao.recentAttempts(userId, 10),
                statsDao.wrongTopKnowledge(userId, 5)
        );
    }
}
