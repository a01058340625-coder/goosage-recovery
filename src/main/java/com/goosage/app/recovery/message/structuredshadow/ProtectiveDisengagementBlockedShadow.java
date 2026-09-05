package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class ProtectiveDisengagementBlockedShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. App Discovery / View -> Delete
        // ID177
        // ----------------------------------------------------

        boolean appViewDelete =
                containsAny(
                        text,
                        "관련 앱을 찾아서",
                        "앱을 찾아서"
                )
                && containsAny(
                        text,
                        "화면만 잠깐 봤",
                        "화면을 봤"
                )
                && containsAny(
                        text,
                        "그러고 나서 삭제",
                        "앱을 삭제",
                        "삭제했"
                );

        if (appViewDelete) {
            return true;
        }


        // ----------------------------------------------------
        // B. Search Input -> Disengagement
        // ID222
        // ----------------------------------------------------

        boolean searchInputDisengagement =
                containsAny(
                        text,
                        "검색창에 사이트 이름만 적어봤",
                        "검색창에 사이트 이름을 적"
                )
                && containsAny(
                        text,
                        "다른 일을 했",
                        "더 이상 하지 않"
                );

        if (searchInputDisengagement) {
            return true;
        }


        // ----------------------------------------------------
        // C. Search Result View -> Stop
        // ID495
        // ----------------------------------------------------

        boolean searchResultStop =
                containsAny(
                        text,
                        "검색 결과를 조금 본 뒤",
                        "검색 결과를 본 뒤"
                )
                && containsAny(
                        text,
                        "그만뒀",
                        "그만두"
                );

        if (searchResultStop) {
            return true;
        }


        // ----------------------------------------------------
        // D. Reinstall Thought -> Explicit Non-Install
        // ID495
        // ----------------------------------------------------

        boolean reinstallNonExecution =
                containsAny(
                        text,
                        "다시 설치할까",
                        "재설치할까"
                )
                && containsAny(
                        text,
                        "설치 버튼은 누르지 않",
                        "설치하지 않"
                );

        if (reinstallNonExecution) {
            return true;
        }

        return false;
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
