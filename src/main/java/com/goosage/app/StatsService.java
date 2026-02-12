package com.goosage.app;

import org.springframework.stereotype.Service;

import com.goosage.domain.stats.StatsPort;
import com.goosage.dto.StatsOverviewResponse;

@Service
public class StatsService {

    private final StatsPort statsPort;

    public StatsService(StatsPort statsPort) {
        this.statsPort = statsPort;
    }

    public StatsOverviewResponse overview(long userId, int days) {
        return statsPort.overview(userId, days);
    }
}
