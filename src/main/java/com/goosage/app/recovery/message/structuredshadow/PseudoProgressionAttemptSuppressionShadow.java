package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class PseudoProgressionAttemptSuppressionShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        boolean hasAccountControlContext = false;

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            if (
                    containsAny(
                            text,
                            "차단",
                            "해제",
                            "계정"
                    )
            ) {
                hasAccountControlContext = true;
            }
        }

        for (StructuredEventShadow structured : events) {

            String text = structured.text();

            String action =
                    String.valueOf(
                            structured.event().actionType()
                    );

            String stage =
                    String.valueOf(
                            structured.event().actionStage()
                    );


            // 1. Account-Control search
            if (
                    "SEARCH".equals(action)
                    && text.contains("해제")
                    && containsAny(
                            text,
                            "방법",
                            "차단",
                            "계정"
                    )
            ) {
                return true;
            }


            // 2. Login Screen only after Account-Control context
            if (
                    hasAccountControlContext
                    && text.contains("로그인 화면까지")
                    && containsAny(
                            text,
                            "아직 로그인",
                            "로그인하지",
                            "돈을 넣지",
                            "베팅하지"
                    )
            ) {
                return true;
            }


            // 3. Explicit login negation
            if (
                    "LOGIN".equals(action)
                    && "STARTED".equals(stage)
                    && containsAny(
                            text,
                            "로그인 화면까지 간 것도 아니",
                            "로그인한 건 아니"
                    )
            ) {
                return true;
            }


            // 4. Funding thought misclassified as STARTED
            if (
                    "FUNDING".equals(action)
                    && "STARTED".equals(stage)
                    && text.contains("얼마를 넣을지")
                    && text.contains("생각")
            ) {
                return true;
            }


            // 5. Recovery contact lookup
            if (
                    "SEARCH".equals(action)
                    && text.contains("연락처")
                    && containsAny(
                            text,
                            "누나",
                            "가족",
                            "상담",
                            "혼자"
                    )
            ) {
                return true;
            }
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
