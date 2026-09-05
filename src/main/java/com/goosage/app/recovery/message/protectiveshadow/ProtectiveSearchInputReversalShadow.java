package com.goosage.app.recovery.message.protectiveshadow;

public class ProtectiveSearchInputReversalShadow {

    public boolean resolve(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        boolean searchContext =
                containsAny(
                        text,
                        "검색창",
                        "검색어",
                        "사이트 이름",
                        "사이트 주소"
                );

        boolean inputProgression =
                containsAny(
                        text,
                        "몇 글자",
                        "입력",
                        "쳤다가",
                        "썼다가"
                );

        boolean reversal =
                containsAny(
                        text,
                        "지웠",
                        "지웠습니다",
                        "더 안 눌렀",
                        "결과가 뜨기 전에"
                );

        return searchContext
                && inputProgression
                && reversal;
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
