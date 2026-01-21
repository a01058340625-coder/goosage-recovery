// src/main/java/com/goosage/controller/StatsController.java
package com.goosage.controller;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.dao.StatsDao;
import com.goosage.dto.StatsOverviewResponse;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatsController {

    private final StatsDao statsDao;

    public StatsController(StatsDao statsDao) {
        this.statsDao = statsDao;
    }

    /**
     * 📊 학습 통계 Overview
     * GET /stats/overview?days=7|30
     */
    @GetMapping("/stats/overview")
    public ApiResponse<StatsOverviewResponse> overview(
            @RequestParam(defaultValue = "30") int days,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("UNAUTHORIZED");
        }

        StatsOverviewResponse res = new StatsOverviewResponse(
                userId,
                statsDao.attemptsInDays(userId, days),
                statsDao.avgScoreInDays(userId, days),
                statsDao.attemptsToday(userId),
                statsDao.avgScoreToday(userId),
                statsDao.recentAttempts(userId, 10),
                statsDao.wrongTopKnowledge(userId, 5)
        );

        return ApiResponse.ok(res);
    }
}
