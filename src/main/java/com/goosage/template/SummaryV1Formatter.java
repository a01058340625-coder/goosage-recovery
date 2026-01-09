package com.goosage.template;

import java.util.*;
import java.util.stream.Collectors;

public class SummaryV1Formatter {

    private final ActionRuleEngine actionRuleEngine = new ActionRuleEngine();

    public String format(String subject, String title, String content, List<String> tags) {

        List<String> summary3 = buildSummary3Lines(subject, title, content);
        List<String> points3 = buildPoints(tags, subject, title);
        List<String> actions = actionRuleEngine.generateActions(tags, subject, title);

        StringBuilder sb = new StringBuilder();
        sb.append("[요약]\n");
        for (String s : summary3) sb.append("- ").append(s).append("\n");

        sb.append("\n[핵심 포인트]\n");
        for (String p : points3) sb.append("- ").append(p).append("\n");

        sb.append("\n[다음 액션]\n");
        for (String a : actions) sb.append("- ").append(a).append("\n");

        return sb.toString().trim();
    }

    // ✅ 규칙: content 우선, 문장 분리 후 3개
    private List<String> buildSummary3Lines(String subject, String title, String content) {
        List<String> candidates = new ArrayList<>();

        // 1) content에서 문장 후보 뽑기
        if (content != null && !content.isBlank()) {
            candidates.addAll(splitSentences(content));
        }

        // 2) 길이/중복 필터 + 상위 3개
        List<String> picked = candidates.stream()
                .map(String::trim)
                .filter(s -> s.length() >= 20)
                .filter(s -> !s.isBlank())
                .distinct()
                .limit(3)
                .collect(Collectors.toList());

        // 3) 부족하면 title/subject로 보충
        if (picked.size() < 3) {
            if (title != null && !title.isBlank()) picked.add(title.trim());
        }
        if (picked.size() < 3) {
            if (subject != null && !subject.isBlank()) picked.add(subject.trim());
        }

        // 4) 그래도 없으면 기본 문구
        if (picked.isEmpty()) {
            return List.of("요약할 내용이 부족합니다.");
        }

        // 5) 3줄 고정 (부족하면 있는 만큼)
        return picked.size() > 3 ? picked.subList(0, 3) : picked;
    }

    private List<String> buildPoints(List<String> tags, String subject, String title) {
        List<String> points = new ArrayList<>();

        // tags 우선
        if (tags != null) {
            for (String t : tags) {
                if (t == null) continue;
                String v = t.trim();
                if (!v.isBlank()) points.add(v);
            }
        }

        // 부족하면 subject/title에서 보충(아주 약하게)
        if (points.size() < 3) {
            if (subject != null && !subject.isBlank()) points.add(subject.trim());
        }
        if (points.size() < 3) {
            if (title != null && !title.isBlank()) points.add(title.trim());
        }

        // 중복 제거 + 3개
        return points.stream().distinct().limit(3).collect(Collectors.toList());
    }

    private List<String> splitSentences(String text) {
        // V1: ".", "\n" 기준
        String normalized = text.replace("\r", "\n");
        String[] parts = normalized.split("[\\.\\n]");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            String s = p.trim();
            if (!s.isBlank()) out.add(s);
        }
        return out;
    }
}
