package com.goosage.app.recovery.message.protectiveshadow;

public class CompletedProtectiveBlockShadow {

    public boolean resolve(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        return containsAny(
                text,
                "계정을 막",
                "다시 계정을 막",
                "계정을 다시 차단",
                "계정을 차단",
                "사이트를 차단",
                "다시 차단"
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
}
