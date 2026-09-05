package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class StrongDesireTemporalGuardedUrgeShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean historical = false;
        boolean historicalDesire = false;
        boolean currentDesire = false;
        boolean currentProtectiveState = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (hasCurrentReset(text)) {
                historical = false;
            }

            if (hasPastStart(text)) {
                historical = true;
            }

            if (hasCurrentProtectiveState(text)) {
                currentProtectiveState = true;
            }

            if (!isStrongDesire(text)) {
                continue;
            }

            if (historical) {
                historicalDesire = true;
            }

            if (!historical) {
                currentDesire = true;
            }
        }

        if (
                hasActionPurposeOnly(events)
                && hasSubmittedOrCompletedWager(events)
        ) {
            return false;
        }

        if (currentDesire) {
            return true;
        }

        if (
                historicalDesire
                && currentProtectiveState
        ) {
            return false;
        }

        return historicalDesire;
    }


    private boolean hasActionPurposeOnly(
            List<StructuredEventShadow> events
    ) {
        boolean found = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (
                    text.contains("베팅을 하려고")
                    || text.contains("베팅하려고")
            ) {
                found = true;
                continue;
            }

            if (isStrongDesire(text)) {
                return false;
            }
        }

        return found;
    }


    private boolean hasSubmittedOrCompletedWager(
            List<StructuredEventShadow> events
    ) {
        for (StructuredEventShadow structured : events) {

            if (
                    "WAGER".equals(
                            String.valueOf(
                                    structured.event().actionType()
                            )
                    )
                    && (
                        "SUBMITTED".equals(
                                String.valueOf(
                                        structured.event().actionStage()
                                )
                        )
                        || "COMPLETED".equals(
                                String.valueOf(
                                        structured.event().actionStage()
                                )
                        )
                    )
            ) {
                return true;
            }
        }

        return false;
    }

    private boolean isStrongDesire(String text) {

        if (hasAccountControlContext(text)) {
            return false;
        }

        if (hasEventNegation(text)) {
            return false;
        }

        return containsAny(
                text,
                "해볼까",
                "해보고 싶",
                "하고 싶은 마음",
                "하고 싶어",
                "하고싶어",
                "걸어볼까",
                "걸어보고 싶",
                "만회해야",
                "만회하려고",
                "만회하고 싶",
                "되찾고 싶",
                "다시 벌 수",
                "따면 해결",
                "다시 나올 것 같",
                "욕심이 생겨",
                "아쉬운 마음",
                "멈추기 싫",
                "조금씩 흔들렸",
                "마음이 흔들렸",

                "한 번만 해보자",
                "한번만 해보자",
                "베팅할까",
                "베팅을 하려고",
                "베팅하려고",
                "걸어 볼까",
                "입금할까",
                "추가 입금을 할까",
                "다시 설치할까",
                "메울 수 있지 않을까",
                "만회하려는 생각",
                "더 해도 될 것 같",
                "들어가고 싶은 마음",
                "싶은 마음이 확 올라"
        );
    }


    private boolean hasEventNegation(String text) {
        return containsAny(
                text,
                "하고 싶은 마음이 들지 않",
                "하고 싶은 마음은 없",
                "하고 싶은 마음도 없",
                "들어가고 싶은 마음도 없",
                "하고 싶은 생각은 없",
                "다시 하고 싶지 않",
                "베팅을 하려고 들어간 건 아니",
                "베팅하려고 들어간 건 아니"
        );
    }


    private boolean hasAccountControlContext(String text) {
        return containsAny(
                text,
                "해제",
                "고객센터",
                "차단 풀"
        );
    }


    private boolean hasPastStart(String text) {
        return containsAny(
                text,
                "몇 달 전",
                "몇 주 전",
                "한 달 전",
                "몇 년 전",
                "예전에"
        );
    }


    private boolean hasCurrentReset(String text) {
        return containsAny(
                text,
                "오늘",
                "지금",
                "어제",
                "이번 주",
                "최근"
        );
    }


    private boolean hasCurrentProtectiveState(String text) {
        return containsAny(
                text,
                "지금은 사이트를 차단",
                "지금은 계정을 차단",
                "지금은 차단해 놓은 상태"
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
