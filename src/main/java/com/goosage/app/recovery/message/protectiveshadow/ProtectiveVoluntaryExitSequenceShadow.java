package com.goosage.app.recovery.message.protectiveshadow;

import java.util.List;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.structuredshadow.StructuredEventShadow;

public class ProtectiveVoluntaryExitSequenceShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {
        boolean candidate = false;
        boolean riskOpen = false;

        for (StructuredEventShadow structured : events) {

            ActionType action =
                    structured.event().actionType();

            ActionStage stage =
                    structured.event().actionStage();

            String text =
                    structured.text();

            if (isExternal(text)) {
                candidate = false;
                riskOpen = false;
                continue;
            }

            if (isWagerCompletion(structured)) {
                candidate = false;
                riskOpen = false;
                continue;
            }

            if (
                    isRiskAction(action)
                    && stage != ActionStage.THOUGHT
                    && stage != ActionStage.UNKNOWN
                    && stage != ActionStage.COMPLETED
            ) {
                riskOpen = true;
            }

            if (
                    riskOpen
                    && isSelfExit(text)
                    && !isThirdPartyExit(text)
                    && !isNaturalExit(text)
            ) {
                candidate = true;
                riskOpen = false;
            }
        }

        return candidate;
    }

    private boolean isRiskAction(ActionType action) {
        return action == ActionType.SEARCH
                || action == ActionType.ACCESS
                || action == ActionType.LOGIN
                || action == ActionType.FUNDING
                || action == ActionType.WAGER;
    }

    private boolean isSelfExit(String text) {
        return containsAny(
                text,
                "닫았",
                "닫고",
                "껐",
                "종료",
                "나왔",
                "나왔다",
                "그만뒀"
        );
    }

    private boolean isExternal(String text) {
        return containsAny(
                text,
                "전화가 와",
                "전화가 왔",
                "동생이",
                "배우자가",
                "업무 전화",
                "회사에서 전화",
                "배터리",
                "오류가 나",
                "오류가 떠",
                "가족에게서 연락",
                "출근 알림",
                "친구한테 전화",
                "전화를 받",
                "알림이 울"
        );
    }

    private boolean isThirdPartyExit(String text) {
        return (
                text.contains("친구")
                || text.contains("지인")
                || text.contains("동료")
                || text.contains("배우자")
                || text.contains("형")
                || text.contains("동생")
        ) && text.contains("그만");
    }

    private boolean isNaturalExit(String text) {
        return (
                text.contains("정보만")
                && text.contains("보고 나왔")
        )
                || (
                text.contains("조금 보고 나왔")
        )
                || (
                text.contains("그냥")
                && text.contains("보고 나왔")
        )
                || (
                text.contains("잠깐")
                && text.contains("보고 나왔")
        )
                || (
                text.contains("화면을 닫고")
                && text.contains("먹")
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

        if (
                containsAny(
                        text,
                        "성립되지 않",
                        "베팅하지 않",
                        "걸지 않"
                )
        ) {
            return false;
        }

        return containsAny(
                text,
                "돌렸",
                "판이 끝",
                "베팅이 성립",
                "베팅을 완료",
                "베팅이 완료",
                "실제로 베팅",
                "베팅은 실패"
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
