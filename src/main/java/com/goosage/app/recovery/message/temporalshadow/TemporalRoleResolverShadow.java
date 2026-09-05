package com.goosage.app.recovery.message.temporalshadow;

public class TemporalRoleResolverShadow {

    public TemporalRoleShadow resolve(String text) {
        if (text == null || text.isBlank()) {
            return TemporalRoleShadow.UNKNOWN;
        }

        if (containsHabitual(text)) {
            return TemporalRoleShadow.HABITUAL;
        }

        if (containsRecentPast(text)) {
            return TemporalRoleShadow.RECENT_PAST;
        }

        if (containsPast(text)) {
            return TemporalRoleShadow.PAST;
        }

        if (containsCurrent(text)) {
            return TemporalRoleShadow.CURRENT;
        }

        return TemporalRoleShadow.UNKNOWN;
    }

    private boolean containsHabitual(String text) {
        return text.contains("계속 반복")
                || text.contains("다시 하게 돼")
                || text.contains("또 베팅하게 돼")
                || text.contains("돈만 생기면 다시")
                || text.contains("계속 하게 됐")
                || text.contains("자연스럽게 배팅")
                || text.contains("자연스럽게 베팅");
    }

    private boolean containsRecentPast(String text) {
        return text.contains("어제")
                || text.contains("어젯밤")
                || text.contains("어제 밤");
    }
    private boolean containsPast(String text) {
        return text.contains("지난주")
                || text.contains("지난달")
                || text.contains("며칠 전")
                || text.contains("예전에")
                || text.contains("처음 ")
                || text.contains("그때")
                || text.contains("한동안");
    }

    private boolean containsCurrent(String text) {
        return text.contains("오늘")
                || text.contains("지금")
                || text.contains("현재")
                || text.contains("이번에는");
    }
}
