package com.goosage.domain.stats;

import com.goosage.dto.StatsOverviewResponse;

public interface StatsPort {
    StatsOverviewResponse overview(long userId, int days);
}
