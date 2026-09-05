package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class CompletedProtectiveRecoveryActionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Funding Reversal
        // ID22
        // ----------------------------------------------------

        boolean fundingReversal =
                containsAny(
                        text,
                        "계좌에 돈을 옮겨뒀",
                        "돈을 옮겨두"
                )
                && containsAny(
                        text,
                        "생각을 바꾸고",
                        "마음을 바꾸"
                )
                && containsAny(
                        text,
                        "다시 빼냈",
                        "다시 빼"
                );

        if (fundingReversal) {
            return true;
        }


        // ----------------------------------------------------
        // B. Funding Non-Execution by deliberate reversal
        // ID26
        // ----------------------------------------------------

        boolean fundingCancel =
                containsAny(
                        text,
                        "돈을 옮기려다가",
                        "이체하려다가"
                )
                && containsAny(
                        text,
                        "마음을 바꿔서",
                        "생각을 바꾸고"
                )
                && containsAny(
                        text,
                        "이체하지 않",
                        "돈을 옮기지 않"
                );

        if (fundingCancel) {
            return true;
        }


        // ----------------------------------------------------
        // C. Explicit Social Boundary
        // ID176
        // ----------------------------------------------------

        boolean socialBoundary =
                containsAny(
                        text,
                        "그런 얘기는 하지 말라고",
                        "도박 얘기는 하지 말라고"
                )
                && containsAny(
                        text,
                        "메시지를 보냈",
                        "문자를 보냈"
                );

        if (socialBoundary) {
            return true;
        }


        // ----------------------------------------------------
        // D. Disclosure + Payment Method Handoff
        // ID475
        // ----------------------------------------------------

        boolean disclosureHandoff =
                containsAny(
                        text,
                        "가족에게",
                        "배우자"
                )
                && containsAny(
                        text,
                        "이야기했",
                        "말했",
                        "알렸"
                )
                && containsAny(
                        text,
                        "카드는 건네주었",
                        "카드를 건네주었",
                        "결제카드를 잠시 맡"
                );

        if (disclosureHandoff) {
            return true;
        }


        // ----------------------------------------------------
        // E. Protective Alternative Action
        // ID492
        // ----------------------------------------------------

        boolean protectiveAlternative =
                containsAny(
                        text,
                        "휴대폰을 내려놓",
                        "휴대폰을 내려놨"
                )
                && containsAny(
                        text,
                        "산책을 나갔",
                        "산책하러 나갔"
                )
                && containsAny(
                        text,
                        "생각이 많이 가라앉",
                        "마음이 가라앉"
                );

        if (protectiveAlternative) {
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
