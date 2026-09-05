package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class CompletedAccountBlockRecoveryShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            if (structured == null) {
                continue;
            }

            String text = structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            String action = String.valueOf(
                    structured.event().actionType()
            );

            String stage = String.valueOf(
                    structured.event().actionStage()
            );

            if (!"ACCOUNT_CONTROL".equals(action)) {
                continue;
            }

            if (!"COMPLETED".equals(stage)) {
                continue;
            }

            if (isAccountBlockReversal(text)) {
                continue;
            }

            if (!hasCompletedAccountBlockEvidence(text)) {
                continue;
            }

            return true;
        }

        return false;
    }


    private boolean hasCompletedAccountBlockEvidence(
            String text
    ) {

        boolean blockTarget =
                containsAny(
                        text,
                        "계정 차단",
                        "계정을 차단",
                        "계정을 막",
                        "차단 신청",
                        "차단 요청",
                        "차단 처리"
                );

        boolean completionEvidence =
                containsAny(
                        text,
                        "차단이 완료",
                        "차단 완료",
                        "차단됐",
                        "차단되었",
                        "차단 처리가 완료",
                        "차단 처리됐",
                        "차단 처리되었",
                        "제출해서 차단",
                        "제출했고 차단",
                        "요청했고 차단",
                        "요청해서 차단"
                );

        return (
                blockTarget
                && completionEvidence
        );
    }


    private boolean isAccountBlockReversal(
            String text
    ) {

        return containsAny(
                text,
                "차단 해제",
                "차단을 해제",
                "차단 취소",
                "차단을 취소",
                "차단 신청 취소",
                "차단 요청 취소",
                "해제 요청",
                "해제 신청",
                "해제가 완료",
                "해제 완료"
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
