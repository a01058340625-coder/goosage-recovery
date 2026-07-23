package com.goosage.app.recovery.message;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;

@Component
public class RuleBasedRecoveryMessageAnalyzer {

    public RecoveryMessageAnalysis analyze(String message) {
        String normalized = normalize(message);

        if (normalized.isBlank()) {
            return hold(message, "EMPTY_MESSAGE");
        }

        if (normalized.length() < 4) {
            return hold(message, "MESSAGE_TOO_SHORT");
        }

        if (looksLikeThirdPartyContext(normalized)) {
            return hold(message, "THIRD_PARTY_CONTEXT");
        }

        if (looksHypothetical(normalized)) {
            return hold(message, "HYPOTHETICAL_CONTEXT");
        }

        int urgeLogDelta = containsAffirmedUrge(normalized) ? 1 : 0;
        int betAttemptDelta = containsAffirmedAttempt(normalized) ? 1 : 0;
        int betBlockedDelta = containsProtectiveBlock(normalized) ? 1 : 0;
        int recoveryActionDelta = containsRecoveryAction(normalized) ? 1 : 0;
        int relapseSignalDelta = containsRelapseSignal(normalized) ? 1 : 0;

        int totalSignals =
                urgeLogDelta
                + betAttemptDelta
                + betBlockedDelta
                + recoveryActionDelta
                + relapseSignalDelta;

        if (totalSignals == 0) {
            return hold(message, "NO_SUPPORTED_SIGNAL");
        }

        double confidence = resolveConfidence(totalSignals);

        RecoveryMessageSignal signal = new RecoveryMessageSignal(
                urgeLogDelta,
                betAttemptDelta,
                betBlockedDelta,
                recoveryActionDelta,
                relapseSignalDelta,
                confidence,
                buildReason(
                        urgeLogDelta,
                        betAttemptDelta,
                        betBlockedDelta,
                        recoveryActionDelta,
                        relapseSignalDelta
                )
        );

        return new RecoveryMessageAnalysis(
                message,
                true,
                signal,
                null
        );
    }

    private RecoveryMessageAnalysis hold(String message, String holdReason) {
        return new RecoveryMessageAnalysis(
                message,
                false,
                null,
                holdReason
        );
    }

    private String normalize(String message) {
        if (message == null) {
            return "";
        }

        return message
                .trim()
                .replaceAll("\\s+", " ");
    }

    private boolean looksLikeThirdPartyContext(String text) {
        return containsAny(
                text,
                "친구가",
                "지인이",
                "동생이",
                "형이",
                "누나가",
                "언니가",
                "오빠가",
                "남편이",
                "아내가",
                "그 사람이"
        );
    }

    private boolean looksHypothetical(String text) {
        return containsAny(
                text,
                "만약",
                "가정하면",
                "그렇다면",
                "할 수도",
                "한다면",
                "했더라면"
        );
    }

    private boolean containsAffirmedUrge(String text) {
        if (containsAny(text, "충동은 없", "충동이 없", "충동 없")) {
            return false;
        }

        return containsAny(
                text,
                "충동이 왔",
                "충동이 생겼",
                "충동을 느꼈",
                "하고 싶었",
                "베팅하고 싶"
        );
    }

    private boolean containsAffirmedAttempt(String text) {
        if (containsAny(
                text,
                "시도하지 않았",
                "들어가지 않았",
                "결제하지 않았",
                "시도하려던 건 아니",
                "시도하려던 것은 아니"
        )) {
            return false;
        }

        return containsAny(
                text,
                "베팅을 시도",
                "결제를 시도",
                "사이트에 들어갔",
                "베팅 화면을 열었",
                "구매를 시도"
        );
    }

    private boolean containsProtectiveBlock(String text) {
        return containsAny(
                text,
                "사이트를 닫",
                "앱을 닫",
                "결제를 멈췄",
                "결제를 취소",
                "차단했",
                "막았",
                "중단했"
        );
    }

    private boolean containsRecoveryAction(String text) {
        return containsAny(
                text,
                "산책했",
                "운동했",
                "전화했",
                "상담했",
                "도움을 요청",
                "회복 행동",
                "일기를 썼",
                "자리를 피했"
        );
    }

    private boolean containsRelapseSignal(String text) {
        if (containsAny(text, "재발하지 않았", "무너지지 않았")) {
            return false;
        }

        return containsAny(
                text,
                "다시 베팅했",
                "재발했",
                "무너졌",
                "결국 결제했",
                "통제하지 못했"
        );
    }

    private boolean containsAny(String text, String... candidates) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    private double resolveConfidence(int totalSignals) {
        if (totalSignals >= 3) {
            return 0.90;
        }

        if (totalSignals == 2) {
            return 0.80;
        }

        return 0.70;
    }

    private String buildReason(
            int urgeLogDelta,
            int betAttemptDelta,
            int betBlockedDelta,
            int recoveryActionDelta,
            int relapseSignalDelta
    ) {
        return "urge=%d, attempt=%d, blocked=%d, recovery=%d, relapse=%d"
                .formatted(
                        urgeLogDelta,
                        betAttemptDelta,
                        betBlockedDelta,
                        recoveryActionDelta,
                        relapseSignalDelta
                );
    }
}