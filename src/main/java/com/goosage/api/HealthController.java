package com.goosage.api;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.support.web.ApiResponse;

/**
 * ✅ GooSage "살아있나?" 확인용 엔드포인트
 *
 * 왜 필요?
 * - 배포/운영/도커/리버스프록시에서 가장 먼저 확인하는 URL
 * - DB, Flyway, 프론트가 없어도 "앱 자체가 살아있는지"를 즉시 검증 가능
 *
 * 규칙:
 * - v0.1에서는 복잡하게 만들지 말고, status만 확실히 내려주면 끝
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {

        // ✅ 응답 순서가 보기 좋게 고정되도록 LinkedHashMap 사용(선택)
        Map<String, Object> data = new LinkedHashMap<>();

        // ✅ 최소 정보: UP이면 서버가 정상 기동 중
        data.put("status", "UP");

        // ✅ 서비스 이름(나중에 여러 서비스 붙을 때 식별에 도움)
        data.put("service", "GooSage API");

        // ✅ 서버 시간 (타임존 문제/서버 재시작 확인에 도움)
        data.put("time", LocalDateTime.now().toString());

        // ✅ 기존 ApiResponse 포맷 유지: success/message/data
        return ApiResponse.ok("service alive", data);
    }
}
