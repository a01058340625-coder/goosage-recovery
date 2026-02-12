package com.goosage.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.StatsService;
import com.goosage.auth.SessionConst;
import com.goosage.dto.StatsOverviewResponse;
import com.goosage.support.web.ApiResponse;

import jakarta.servlet.http.HttpSession;

@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats/overview")
    public ApiResponse<StatsOverviewResponse> overview(
            @RequestParam(defaultValue = "30") int days,
            HttpSession session
    ) {
        Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (userId == null) {
            return ApiResponse.fail("UNAUTHORIZED");
        }

        StatsOverviewResponse res =
                statsService.overview(userId, days);

        return ApiResponse.ok(res);
    }
}
