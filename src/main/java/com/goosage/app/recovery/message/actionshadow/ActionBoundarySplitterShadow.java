package com.goosage.app.recovery.message.actionshadow;

import java.util.ArrayList;
import java.util.List;

public class ActionBoundarySplitterShadow {

    private static final String[] MARKERS = {
            "사이트에 들어갔다가",
            "사이트에 들어가",
            "돈을 걸기 직전에",
            "다시 계정을 막았",
            "입금 화면",
            "베팅 화면",
            "로그인한 뒤",
            "슬롯을"
    };

    public List<String> split(String text) {
        List<String> result = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return result;
        }

        String remaining = text.trim();

        while (!remaining.isBlank()) {
            int index = findNextMarker(remaining);

            if (index <= 0) {
                result.add(remaining);
                break;
            }

            String before = remaining.substring(0, index).trim();

            if (!before.isBlank()) {
                result.add(before);
            }

            remaining = remaining.substring(index).trim();
        }

        return result;
    }

    private int findNextMarker(String text) {
        int best = -1;

        for (String marker : MARKERS) {
            int index = text.indexOf(marker);

            if (index > 0 && (best < 0 || index < best)) {
                best = index;
            }
        }

        return best;
    }
}