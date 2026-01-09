package com.goosage.template;

import java.util.*;
import java.util.stream.Collectors;

public class ActionRuleEngine {

    // ✅ V1: 태그 기반 규칙 테이블(하드코딩) — 안정성 우선
    private final List<ActionRule> rules = List.of(
            // db + flyway
            ActionRule.ofAllTags(Set.of("db", "flyway"),
                    List.of("migration 재실행", "DB user/privilege 점검", "flyway_schema_history 상태 확인")),

            // springboot + auth
            ActionRule.ofAllTags(Set.of("springboot", "auth"),
                    List.of("로그인 예외 케이스 테스트(잘못된 비번/없는 이메일)", "세션 유지 확인", "로그아웃 후 접근 차단 확인")),

            // api + json
            ActionRule.ofAllTags(Set.of("api", "json"),
                    List.of("요청/응답 DTO 스키마 점검", "400/500 케이스 에러 바디 표준화", "Postman/PowerShell 시나리오 저장"))
    );

    private final List<String> defaultActions = List.of(
            "관련 내용 3줄 요약 업데이트",
            "다음 학습 주제 1개 선정",
            "재현 가능한 테스트 시나리오(요청 3개) 저장"
    );

    /**
     * ✅ tags + subject/title 기반으로 다음 액션 생성
     * - rules 매칭되면 해당 액션 우선
     * - 아니면 subject/title 키워드로 약한 매칭
     * - 그래도 없으면 default
     */
    public List<String> generateActions(List<String> tags, String subject, String title) {
        Set<String> tagSet = normalizeTags(tags);

        // 1) 태그 룰 매칭
        for (ActionRule rule : rules) {
            if (rule.matches(tagSet)) {
                return rule.actions;
            }
        }

        // 2) 키워드 기반 약한 매칭(subject/title)
        String text = ((subject == null ? "" : subject) + " " + (title == null ? "" : title)).toLowerCase(Locale.ROOT);
        if (text.contains("flyway")) {
            return List.of("migration 재실행", "DB user/privilege 점검");
        }
        if (text.contains("login") || text.contains("auth") || text.contains("세션")) {
            return List.of("로그인/로그아웃 시나리오 재검증", "세션 유지/만료 정책 점검");
        }

        // 3) 기본 액션
        return defaultActions;
    }

    private Set<String> normalizeTags(List<String> tags) {
        if (tags == null) return Collections.emptySet();
        return tags.stream()
                .filter(Objects::nonNull)
                .map(t -> t.trim().toLowerCase(Locale.ROOT))
                .filter(t -> !t.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // 내부 룰 모델
    private static class ActionRule {
        final Set<String> requiredAll;
        final List<String> actions;

        private ActionRule(Set<String> requiredAll, List<String> actions) {
            this.requiredAll = requiredAll;
            this.actions = actions;
        }

        static ActionRule ofAllTags(Set<String> requiredAll, List<String> actions) {
            return new ActionRule(requiredAll, actions);
        }

        boolean matches(Set<String> tags) {
            return tags.containsAll(requiredAll);
        }
    }
}
