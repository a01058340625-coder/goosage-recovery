package com.goosage.api.controller.admin;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/kpi")
public class AdminKpiController {

    private final AdminKpiService service;

    public AdminKpiController(AdminKpiService service) {
        this.service = service;
    }

    @GetMapping("/active-users/today")
    public Map<String, Object> activeUsersToday() {
        LocalDate today = LocalDate.now();
        List<Long> userIds = service.findActiveUserIdsToday();
        return Map.of(
                "ok", true,
                "date", today.toString(),
                "activeUsersToday", userIds.size(),
                "userIds", userIds
        );
    }
}