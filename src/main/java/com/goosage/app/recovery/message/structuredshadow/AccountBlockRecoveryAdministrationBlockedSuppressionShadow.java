package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class AccountBlockRecoveryAdministrationBlockedSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean accountBlockAdministrationSeen = false;
        boolean supportedGamblingProgressionSeen = false;
        boolean actualProtectiveStopAfterRiskSeen = false;

        for (StructuredEventShadow structured : events) {

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

            if (isAccountBlockAdministration(text)) {
                accountBlockAdministrationSeen = true;
            }

            if (isSupportedGamblingProgression(
                    action,
                    stage,
                    text
            )) {
                supportedGamblingProgressionSeen = true;
            }

            if (
                    "SELF_STOP".equals(stop)
                    && isRiskAction(
                        action
                    )
            ) {
                actualProtectiveStopAfterRiskSeen = true;
            }
        }

        return (
                accountBlockAdministrationSeen
                && !supportedGamblingProgressionSeen
                && !actualProtectiveStopAfterRiskSeen
        );
    }


    private boolean isAccountBlockAdministration(
            String text
    ) {

        if (containsAny(
                text,
                "차단 해제",
                "해제 요청",
                "해제 신청"
        )) {
            return false;
        }

        return containsAny(
                text,

                "계정을 차단하려고",
                "계정 차단을 요청",
                "계정을 차단해 달라고",
                "계정을 막아 달라고",
                "차단을 요청했",
                "차단 요청을 했",

                "고객센터에 전화해서 차단",
                "고객센터에 연락해서 차단",
                "상담센터에 연락해서 차단"
        );
    }


    private boolean isSupportedGamblingProgression(
            String action,
            String stage,
            String text
    ) {

        if ("WAGER".equals(action)) {

            return !(
                    "UNKNOWN".equals(stage)
                    || "THOUGHT".equals(stage)
            );
        }

        if ("FUNDING".equals(action)) {

            return !(
                    "UNKNOWN".equals(stage)
                    || "THOUGHT".equals(stage)
            );
        }

        if ("LOGIN".equals(action)) {

            return !(
                    "UNKNOWN".equals(stage)
                    || "SCREEN".equals(stage)
                    || "THOUGHT".equals(stage)
            );
        }

        if ("SEARCH".equals(action)) {

            return !(
                    "UNKNOWN".equals(stage)
                    || "THOUGHT".equals(stage)
            );
        }

        if ("APP".equals(action)) {

            return !(
                    "UNKNOWN".equals(stage)
                    || "THOUGHT".equals(stage)
            );
        }

        if ("ACCESS".equals(action)) {
            return true;
        }

        return containsAny(
                text,

                "사이트에 접속",
                "사이트에 들어갔",
                "로그인했",
                "입금 화면",
                "입금 금액",
                "베팅 금액",
                "베팅 버튼",
                "베팅 주문"
        );
    }


    private boolean isRiskAction(
            String action
    ) {

        return (
                "WAGER".equals(action)
                || "FUNDING".equals(action)
                || "LOGIN".equals(action)
                || "SEARCH".equals(action)
                || "APP".equals(action)
                || "ACCESS".equals(action)
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
