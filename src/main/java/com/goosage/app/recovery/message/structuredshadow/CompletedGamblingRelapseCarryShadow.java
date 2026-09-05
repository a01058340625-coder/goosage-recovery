package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class CompletedGamblingRelapseCarryShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean completedGambling = false;

        for (StructuredEventShadow structured : events) {

            String text =
                    structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            if (isExplicitNegativeCompletion(text)) {
                continue;
            }

            if (hasCompletedGamblingEvidence(text)) {
                completedGambling = true;
            }
        }

        return completedGambling;
    }


    private boolean hasCompletedGamblingEvidence(
            String text
    ) {

        return containsAny(
                text,

                // explicit establishment
                "베팅이 실제로 성립",
                "실제 베팅이 성립",
                "베팅이 성립됐",
                "베팅이 성립되",

                // order completion
                "주문까지 정상 처리",
                "주문이 정상 처리",
                "베팅 주문이 정상적으로 처리",
                "베팅 주문이 정상 처리",

                // explicit completed betting
                "실제로 베팅을 했",
                "실제 베팅을 했",
                "베팅을 완료",
                "베팅이 완료",

                // narrative completion
                "첫 베팅이 끝난",
                "두 번째까지 끝난",
                "두번째까지 끝난"
        );
    }


    private boolean isExplicitNegativeCompletion(
            String text
    ) {

        return containsAny(
                text,

                // failed submit
                "주문이 실패",
                "주문 실패",
                "베팅은 성립되지 않았",
                "베팅이 성립되지 않았",

                // canceled before completion
                "완료되기 전에 취소",
                "최종 확인 전에 취소",
                "주문 전에 취소",

                // explicit non-execution
                "베팅은 하지 않았",
                "실제로 베팅하지 않았",
                "베팅을 시작하지 않았"
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
