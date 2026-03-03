package com.goosage.api.controller.internal;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.goosage.app.StudyEventService;
import com.goosage.support.web.ApiResponse;

@RestController
@RequestMapping("/internal/study")
public class InternalStudyController {

    private static final String INTERNAL_KEY = "goosage-dev";

    private final StudyEventService studyEventService;

    public InternalStudyController(StudyEventService studyEventService) {
        this.studyEventService = studyEventService;
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> recordEvent(
            @RequestHeader(value = "X-INTERNAL-KEY", required = false) String key,
            @RequestBody Map<String, Object> body
    ) {
    	
    	System.out.println("[INTERNAL] hit /internal/study/events");
    	System.out.println("[INTERNAL] key=" + key);
    	
        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "bad internal key");
        }

        // body 예:
        // { "userId": 12, "type": "LMS_QUIZ", "knowledgeId": null }

        Long userId = toLong(body.get("userId"));
        String typeStr = (String) body.get("type");
        Long knowledgeId = toLongNullable(body.get("knowledgeId"));

        if (userId == null || typeStr == null || typeStr.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId/type required");
        }

        // ✅ req.type() 자리에 들어가던 타입이 enum이면 valueOf 필요
        // - 만약 StudyEventService.record가 String을 받는 구조면 아래 한 줄을 String로 바꿔
        studyEventService.record(userId, typeStr, knowledgeId);

        return ApiResponse.ok(null);
    }

    private Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        return Long.valueOf(v.toString());
    }

    private Long toLongNullable(Object v) {
        if (v == null) return null;
        String s = v.toString();
        if (s.isBlank() || "null".equalsIgnoreCase(s)) return null;
        return Long.valueOf(s);
    }
}