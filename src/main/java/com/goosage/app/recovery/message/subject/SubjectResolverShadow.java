package com.goosage.app.recovery.message.subject;

public class SubjectResolverShadow {

    private static final String[] THIRD_PARTY = {
            "친구가",
            "친구들이랑",
            "지인이",
            "동생이",
            "형이",
            "누나가",
            "언니가",
            "오빠가",
            "남편이",
            "아내가",
            "배우자가",
            "그 사람이"
    };

    private static final String[] EXPLICIT_SELF = {
            "나는",
            "내가",
            "나도",
            "저는",
            "저도"
    };


    public SubjectResolutionResult resolve(String message) {

        String text = normalize(message);

        if (text.isBlank()) {
            return new SubjectResolutionResult(
                    SubjectType.UNKNOWN,
                    false,
                    "EMPTY"
            );
        }


        int thirdPartyIndex =
                firstIndexOfAny(
                        text,
                        THIRD_PARTY,
                        0
                );


        if (thirdPartyIndex < 0) {

            return new SubjectResolutionResult(
                    SubjectType.SELF,
                    true,
                    "NO_THIRD_PARTY_SUBJECT"
            );
        }


        int explicitSelfIndex =
                firstIndexOfAny(
                        text,
                        EXPLICIT_SELF,
                        thirdPartyIndex + 1
                );


        if (explicitSelfIndex > thirdPartyIndex) {

            return new SubjectResolutionResult(
                    SubjectType.THIRD_PARTY_TRIGGER_TO_SELF,
                    true,
                    "EXPLICIT_SELF_AFTER_THIRD_PARTY"
            );
        }


        if (
                hasThirdPartyTriggerRelativeClause(
                        text,
                        thirdPartyIndex
                )
        ) {

            return new SubjectResolutionResult(
                    SubjectType.THIRD_PARTY_TRIGGER_TO_SELF,
                    true,
                    "THIRD_PARTY_TRIGGER_RELATIVE_CLAUSE"
            );
        }


        if (
                hasReportedThirdPartyAction(
                        text,
                        thirdPartyIndex
                )
        ) {

            return new SubjectResolutionResult(
                    SubjectType.THIRD_PARTY_ONLY,
                    true,
                    "REPORTED_THIRD_PARTY_ACTION"
            );
        }


        return new SubjectResolutionResult(
                SubjectType.THIRD_PARTY_ONLY,
                true,
                "THIRD_PARTY_WITHOUT_SELF_TRANSITION"
        );
    }


    private boolean hasThirdPartyTriggerRelativeClause(
            String text,
            int thirdPartyIndex
    ) {

        String tail =
                text.substring(
                        Math.max(
                                0,
                                thirdPartyIndex
                        )
                );

        return containsAny(
                tail,
                "보내준",
                "알려준",
                "추천해준",
                "공유해준",
                "소개해준"
        );
    }


    private boolean hasReportedThirdPartyAction(
            String text,
            int thirdPartyIndex
    ) {

        String tail =
                text.substring(
                        Math.max(
                                0,
                                thirdPartyIndex
                        )
                );

        return containsAny(
                tail,
                "했다고 했",
                "다고 했",
                "라고 했",
                "말했",
                "얘기했",
                "이야기했",
                "들었"
        );
    }


    private int firstIndexOfAny(
            String text,
            String[] candidates,
            int startIndex
    ) {

        int first = -1;

        for (String candidate : candidates) {

            int index =
                    text.indexOf(
                            candidate,
                            Math.max(
                                    0,
                                    startIndex
                            )
                    );

            if (
                    index >= 0
                    && (
                            first < 0
                            || index < first
                    )
            ) {
                first = index;
            }
        }

        return first;
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


    private String normalize(
            String message
    ) {

        if (message == null) {
            return "";
        }

        return message
                .trim()
                .replaceAll(
                        "\\s+",
                        " "
                );
    }
}