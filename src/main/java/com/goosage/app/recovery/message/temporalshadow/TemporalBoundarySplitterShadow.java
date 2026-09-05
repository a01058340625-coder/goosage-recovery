package com.goosage.app.recovery.message.temporalshadow;

import java.util.ArrayList;
import java.util.List;

public class TemporalBoundarySplitterShadow {

    private static final String[] MARKERS = {
            "오늘 아침",
            "오늘 오후",
            "오늘 저녁",
            "다음날",
            "다음 날",
            "그날 아침",
            "그날 저녁",
            "어제 아침",
            "어제 밤",
            "어젯밤",
            "지난주",
            "지난 주",
            "지난달",
            "지난 달"
    };

    public List<String> split(String text) {
        List<String> result = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return result;
        }

        int splitIndex = findPastToCurrentTransition(text);

        if (splitIndex <= 0) {
            splitIndex = findInternalMarker(text);
        }

        if (splitIndex <= 0) {
            result.add(text.trim());
            return result;
        }

        String before = text.substring(0, splitIndex).trim();
        String after = text.substring(splitIndex).trim();

        if (!before.isBlank()) {
            result.add(before);
        }

        if (!after.isBlank()) {
            result.add(after);
        }

        return result;
    }

    private int findPastToCurrentTransition(String text) {

        if (text == null || text.isBlank()) {
            return -1;
        }

        String yesterday =
                "\uc5b4\uc81c";

        String today =
                "\uc624\ub298";

        int pastIndex =
                text.indexOf(yesterday);

        if (pastIndex < 0) {
            return -1;
        }

        int currentIndex =
                text.indexOf(
                        today,
                        pastIndex + yesterday.length()
                );

        if (currentIndex <= 0) {
            return -1;
        }

        String beforeCurrent =
                text.substring(
                        0,
                        currentIndex
                ).trim();

        /*
         * Narrow contract:
         *
         *   yesterday ... but today ...
         *
         * Generic yesterday/today co-occurrence alone is
         * intentionally insufficient.
         *
         * Example rejected:
         *   "Yesterday's story was heard again today."
         */
        if (!hasPastToCurrentContrastBoundary(
                beforeCurrent
        )) {
            return -1;
        }

        return currentIndex;
    }


    private boolean hasPastToCurrentContrastBoundary(
            String prefix
    ) {

        return prefix.endsWith(
                "\uc9c0\ub9cc"
        )
                || prefix.endsWith(
                "\ud588\uc9c0\ub9cc"
        )
                || prefix.endsWith(
                "\uc5c8\uc9c0\ub9cc"
        )
                || prefix.endsWith(
                "\uc558\uc9c0\ub9cc"
        );
    }



    private int findInternalMarker(String text) {
        int best = -1;

        for (String marker : MARKERS) {
            int index = text.indexOf(marker);

            if (index > 0) {
                String prefix = text.substring(0, index).trim();

                if (isConnectorOnly(prefix)) {
                    continue;
                }

                if (best < 0 || index < best) {
                    best = index;
                }
            }
        }

        return best;
    }
    private boolean isConnectorOnly(String text) {
        return text.equals("그런데")
                || text.equals("그리고")
                || text.equals("그래서")
                || text.equals("하지만")
                || text.equals("그러나");
    }
}