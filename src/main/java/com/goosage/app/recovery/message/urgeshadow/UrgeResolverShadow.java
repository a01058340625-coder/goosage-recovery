package com.goosage.app.recovery.message.urgeshadow;

import java.util.ArrayList;
import java.util.List;

public class UrgeResolverShadow {

    public UrgeResolutionResult resolve(String message) {
        String normalized = normalize(message);

        if (normalized.isBlank()) {
            return zero();
        }

        if (
                containsAny(
                        normalized,
                        "하고 싶은 마음이 들지 않았",
                        "다시 들어가고 싶은 마음도 없",
                        "다시 시도하고 싶은 생각도 없",
                        "당장 행동할 생각은 없",
                        "다시 할 생각은 없"
                )
        ) {
            return zero();
        }

        List<String> evidence =
                findEvidence(
                        normalized,
                        "다시 해볼까",
                        "다시 하고 싶",
                        "하고 싶은 마음",
                        "계속 생각나",
                        "본전 생각",
                        "만회하고 싶",
                        "조금 걸어볼까",
                        "더 넣을까",
                        "다시 들어가고 싶",
                        "한 번 더 들어갈까",
                        "풀고 싶은 생각"
                );

        if (!evidence.isEmpty()) {
            return new UrgeResolutionResult(
                    1,
                    0.90,
                    List.copyOf(evidence)
            );
        }

        return zero();
    }

    private UrgeResolutionResult zero() {
        return new UrgeResolutionResult(
                0,
                0.20,
                List.of()
        );
    }

    private boolean containsAny(
            String text,
            String... candidates
    ) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }

        return false;
    }

    private List<String> findEvidence(
            String text,
            String... candidates
    ) {
        List<String> result = new ArrayList<>();

        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                result.add(candidate);
            }
        }

        return result;
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.trim().replaceAll("\\s+", " ");
    }
}
