package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveAccessViewExitSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        boolean riskSeen = false;
        boolean candidate = false;

        for (StructuredEventShadow structured : events) {

            ActionType action =
                    structured.event().actionType();

            ActionStage stage =
                    structured.event().actionStage();

            String text =
                    structured.text();

            if (isExternal(text)) {
                riskSeen = false;
                candidate = false;
                continue;
            }

            if (isWagerCompletion(structured)) {
                riskSeen = false;
                candidate = false;
                continue;
            }

            if (
                    isRiskAction(action)
                    && stage != ActionStage.COMPLETED
            ) {
                riskSeen = true;
            }

            if (
                    riskSeen
                    && isView(text)
                    && isExit(text)
            ) {
                if (isNaturalInfoExit(text)) {
                    continue;
                }

                candidate = true;
                riskSeen = false;
                continue;
            }

            if (
                    candidate
                    && isRiskAction(action)
                    && stage != ActionStage.THOUGHT
                    && stage != ActionStage.UNKNOWN
            ) {
                candidate = false;
                riskSeen = true;
            }
        }

        return candidate;
    }

    private boolean isRiskAction(ActionType action) {
        return action == ActionType.SEARCH
                || action == ActionType.ACCESS
                || action == ActionType.LOGIN
                || action == ActionType.WAGER;
    }

    private boolean isView(String text) {
        return containsAny(
                text,
                "화면",
                "정보",
                "배당",
                "사이트",
                "로그인 화면"
        );
    }

    private boolean isExit(String text) {
        return containsAny(
                text,
                "바로 껐",
                "바로 닫",
                "닫았",
                "껐",
                "보고 나왔",
                "잠깐 보고"
        );
    }

    private boolean isNaturalInfoExit(String text) {
        return text.contains("정보만")
                && text.contains("보고 나왔");
    }

    private boolean isExternal(String text) {
        return containsAny(
                text,
                "전화",
                "알림",
                "배터리",
                "업무",
                "동생",
                "배우자"
        );
    }

    private boolean isWagerCompletion(
            StructuredEventShadow structured
    ) {
        if (
                structured.event().actionType()
                        != ActionType.WAGER
        ) {
            return false;
        }

        if (
                structured.event().actionStage()
                        == ActionStage.COMPLETED
        ) {
            return true;
        }

        String text = structured.text();

        return containsAny(
                text,
                "돌렸",
                "판이 끝",
                "베팅이 성립",
                "베팅을 완료",
                "베팅이 완료",
                "실제로 베팅"
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
