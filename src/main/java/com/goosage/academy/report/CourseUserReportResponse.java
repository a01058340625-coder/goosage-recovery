package com.goosage.academy.report;

import java.time.LocalDateTime;

public record CourseUserReportResponse(
        long userId,
        int done,
        int total,
        int percent,
        LocalDateTime lastActivityAt,
        int wrongSum
) {}
