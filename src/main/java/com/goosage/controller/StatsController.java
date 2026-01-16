package com.goosage.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.auth.SessionConst;
import com.goosage.common.ApiResponse;
import com.goosage.dto.KnowledgeDto;
import com.goosage.dto.StatsOverviewResponse;
import com.goosage.repository.StatsDao;
import com.goosage.service.KnowledgeService;


import jakarta.servlet.http.HttpSession;

@RestController
public class StatsController {

    private final StatsDao statsDao;
    private final KnowledgeService knowledgeService;

    public StatsController(StatsDao statsDao, KnowledgeService knowledgeService) {
        this.statsDao = statsDao;
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/stats/overview")
    public ApiResponse<StatsOverviewResponse> overview(
            @RequestParam(name = "days", defaultValue = "7") int days,
            HttpSession session
    ) {
        Object uidObj = session.getAttribute(SessionConst.LOGIN_USER_ID);
        if (uidObj == null) {
            return ApiResponse.fail("UNAUTHORIZED");
        }

        long userId = (uidObj instanceof Long)
                ? (Long) uidObj
                : Long.parseLong(String.valueOf(uidObj));

        // ✅ v0.8.2: 오답 Top에 "제목" 붙이기
        List<StatsOverviewResponse.WrongTop> top =
                statsDao.wrongTopKnowledgeInDays(userId, days, 5);

        List<StatsOverviewResponse.WrongTopDetail> topDetail = new ArrayList<>();
        for (StatsOverviewResponse.WrongTop t : top) {
            String title;
            try {
                KnowledgeDto k = knowledgeService.mustFindById(t.knowledgeId());
                // ✅ 대표 타이틀: question (지금 구조에서 가장 안전)
                title = "knowledge #" + t.knowledgeId();

            } catch (Exception e) {
                title = "(missing knowledge)";
            }
            topDetail.add(new StatsOverviewResponse.WrongTopDetail(
                    t.knowledgeId(),
                    title,
                    t.wrongCount()
            ));
        }

        StatsOverviewResponse res = new StatsOverviewResponse(
                userId,
                statsDao.attemptsInDays(userId, days),
                statsDao.avgScoreInDays(userId, days),
                statsDao.todayAttempts(userId),
                statsDao.todayAvgScorePercent(userId),
                statsDao.recentAttemptsInDays(userId, days, 10),
                topDetail // ✅ 여기 변경
        );

        return ApiResponse.ok(res);
    }
}
