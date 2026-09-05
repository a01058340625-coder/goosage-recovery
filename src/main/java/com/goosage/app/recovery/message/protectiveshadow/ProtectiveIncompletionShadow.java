package com.goosage.app.recovery.message.protectiveshadow;

public class ProtectiveIncompletionShadow {

    public boolean resolve(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        boolean riskyAccountControl =
                containsAny(
                        text,
                        "계정 해제",
                        "차단 해제",
                        "해제 방법",
                        "해제 문의",
                        "해제 신청",
                        "해제 요청",
                        "고객센터"
                );

        boolean incomplete =
                containsAny(
                        text,
                        "아직",
                        "하지 않았",
                        "하지 않",
                        "보내지 않았",
                        "들어가지 않았",
                        "해제되지 않았",
                        "풀리지 않았"
                );

        return riskyAccountControl && incomplete;
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
}
