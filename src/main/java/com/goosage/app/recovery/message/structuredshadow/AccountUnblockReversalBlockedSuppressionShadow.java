package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class AccountUnblockReversalBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean priorCompletedWagerSeen = false;

        for (StructuredEventShadow structured : events) {

            if (structured == null) {
                continue;
            }

            String text = structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            String action = String.valueOf(
                    structured.event().actionType()
            );

            String stage = String.valueOf(
                    structured.event().actionStage()
            );

            String stop = String.valueOf(
                    structured.event().stopCause()
            );

            if (
                    "WAGER".equals(action)
                    && "COMPLETED".equals(stage)
            ) {
                priorCompletedWagerSeen = true;
                continue;
            }

            if (!"ACCOUNT_CONTROL".equals(action)) {
                continue;
            }

            if (!isSupportedUnblockProgression(stage)) {
                continue;
            }

            if (!"SELF_STOP".equals(stop)) {
                continue;
            }

            if (!hasUnblockDirection(text)) {
                continue;
            }

            if (!hasSelfCancellation(text)) {
                continue;
            }

            if (hasSupportedGamblingExecution(text)) {
                continue;
            }

            if (priorCompletedWagerSeen) {
                continue;
            }

            return true;
        }

        return false;
    }


    private boolean isSupportedUnblockProgression(
            String stage
    ) {

        return (
                "INPUT".equals(stage)
                || "SUBMITTED".equals(stage)
                || "SCREEN".equals(stage)
        );
    }


    private boolean hasUnblockDirection(
            String text
    ) {

        return containsAny(
                text,
                "차단 해제",
                "차단을 해제",
                "차단 풀기",
                "차단을 풀",
                "해제 요청",
                "해제 신청"
        );
    }


    private boolean hasSelfCancellation(
            String text
    ) {

        return containsAny(
                text,
                "취소했",
                "취소했고",
                "취소함",
                "취소했어",
                "그만뒀",
                "그만두었",
                "멈췄",
                "중단했",
                "닫았"
        );
    }


    private boolean hasSupportedGamblingExecution(
            String text
    ) {

        return containsAny(
                text,
                "도박 사이트에 접속",
                "카지노 사이트에 접속",
                "사이트에 들어갔",
                "로그인했",
                "입금 금액",
                "입금 버튼",
                "베팅 금액",
                "베팅 버튼",
                "베팅 주문",
                "베팅했",
                "게임을 시작",
                "슬롯을 돌"
        );
    }


    private boolean containsAny(
            String text,
            String... values
    ) {

        for (String value : values) {

            if (text.contains(value)) {
                return true;
            }
        }

        return false;
    }
}
