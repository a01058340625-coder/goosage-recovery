package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class CompletedHelpSeekingRecoveryShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String text =
                    structured.text();

            if (text == null || text.isBlank()) {
                continue;
            }

            if (isNegativeBoundary(text)) {
                continue;
            }

            if (hasCompletedHelpSeeking(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasCompletedHelpSeeking(
            String text
    ) {

        boolean counselingTarget =
                containsAny(
                        text,
                        "상담센터",
                        "상담 센터",
                        "상담사",
                        "상담",
                        "도움"
                );

        if (!counselingTarget) {
            return false;
        }


        boolean completedContact =
                containsAny(
                        text,
                        "전화했어",
                        "전화했습니다",
                        "전화해서",
                        "연락했어",
                        "연락했습니다",
                        "연락해서",
                        "상담했어",
                        "상담했습니다"
                );


        boolean completedRequest =
                containsAny(
                        text,
                        "상담을 요청했어",
                        "상담 요청했어",
                        "상담을 요청했습니다",
                        "도움을 요청했어",
                        "도움 요청했어",
                        "도움을 요청했습니다",
                        "도움을 요청해서"
                );


        return (
                completedContact
                || completedRequest
        );
    }


    private boolean isNegativeBoundary(
            String text
    ) {

        return containsAny(
                text,

                // thought / intent only
                "상담을 받아볼까",
                "상담받아볼까",
                "상담하려고 생각",
                "상담할까 생각",
                "연락하려고 생각",

                // lookup / save only
                "상담센터 번호를 찾",
                "상담센터 연락처를 찾",
                "상담센터 번호를 검색",
                "상담센터 연락처를 검색",
                "상담센터 번호만 저장",
                "상담센터 연락처만 저장",

                // failed / incomplete contact
                "전화했지만 연결되지",
                "전화를 걸었지만 연결되지",
                "연락했지만 연결되지",
                "전화하려고 했지만",
                "전화를 걸려고 했지만",

                // browsing only
                "상담 문의 화면만",
                "상담 페이지까지만"
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
