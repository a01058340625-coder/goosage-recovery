package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class HistoricalCompletedWagerAttemptSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean completedWagerSeen = false;
        boolean laterCurrentGamblingProgression = false;
        boolean laterNonGamblingCurrentContext = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            if (isCompletedWager(text)) {
                completedWagerSeen = true;
                continue;
            }

            if (!completedWagerSeen) {
                continue;
            }

            if (isLaterSupportedGamblingProgression(text)) {
                laterCurrentGamblingProgression = true;
            }

            if (isLaterNonGamblingCurrentContext(text)) {
                laterNonGamblingCurrentContext = true;
            }
        }

        return (
                completedWagerSeen
                && laterNonGamblingCurrentContext
                && !laterCurrentGamblingProgression
        );
    }


    private boolean isCompletedWager(
            String text
    ) {

        return containsAny(
                text,
                "실제 베팅이 한 번 성립",
                "실제 베팅이 성립",
                "베팅이 실제로 성립",
                "베팅이 성립됐",
                "베팅이 성립되",
                "실제로 베팅을 했",
                "실제 베팅을 했"
        );
    }


    private boolean isLaterNonGamblingCurrentContext(
            String text
    ) {

        return containsAny(
                text,

                // account block / re-block
                "계정을 다시 차단",
                "계정 다시 차단",
                "차단했어",
                "차단했습니다",

                // account unblock control
                "해제 요청 버튼",
                "해제 신청서",
                "해제 요청",
                "해제 방법",
                "차단 해제",

                // current urge negation
                "다시 하고 싶은 생각은 들지 않았",
                "다시 하고 싶은 생각은 전혀 들지 않았",
                "다시 할 생각은 없",
                "하고 싶은 생각은 없"
        );
    }


    private boolean isLaterSupportedGamblingProgression(
            String text
    ) {

        return containsAny(
                text,

                // new gambling search
                "베팅 사이트를 검색",
                "카지노 사이트를 검색",
                "도박 사이트를 검색",

                // new site/app access
                "사이트에 다시 들어",
                "사이트에 접속",
                "앱을 다시 실행",
                "베팅 앱을 실행",

                // login progression
                "로그인했",
                "로그인 버튼",
                "비밀번호를 입력",

                // funding progression
                "입금 화면",
                "입금 금액",
                "돈을 입금",
                "이체",

                // wager progression
                "베팅 버튼을 눌렀",
                "베팅 금액을 입력",
                "베팅 주문",
                "경기를 선택"
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
