package com.goosage.app.recovery.message;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;
import com.goosage.domain.recovery.message.RecoveryRiskPreparationMetadata;

@Component
public class RuleBasedRecoveryMessageAnalyzer {

    public RecoveryMessageAnalysis analyze(String message) {
        String normalized = normalize(message);

        if (normalized.isBlank()) {
            return hold(message, "EMPTY_MESSAGE");
        }

        if (normalized.length() < 4) {
            return hold(message, "MESSAGE_TOO_SHORT");
        }

        String analysisText = normalized;
        boolean selfContextExtracted = false;
        boolean currentContextExtracted = false;

        if (looksLikeThirdPartyContext(normalized)) {
            int selfSubjectIndex =
                    findExplicitSelfSubjectAfterThirdParty(normalized);

            if (selfSubjectIndex < 0) {
                return hold(message, "THIRD_PARTY_CONTEXT");
            }

            analysisText = normalized.substring(selfSubjectIndex);
            selfContextExtracted = true;
        }

        String currentContext =
                extractCurrentContextAfterLongPast(
                        analysisText
                );

        if (currentContext != null) {
            analysisText = currentContext;
            currentContextExtracted = true;
        }

        if (looksHypothetical(analysisText)) {
            return hold(message, "HYPOTHETICAL_CONTEXT");
        }

        RecoveryRiskPreparationMetadata riskPreparationMetadata =
                resolveRiskPreparationMetadata(analysisText);

        int urgeLogDelta = containsAffirmedUrge(analysisText) ? 1 : 0;
        int betAttemptDelta = containsAffirmedAttempt(analysisText) ? 1 : 0;
        int betBlockedDelta = containsProtectiveBlock(analysisText) ? 1 : 0;
        int recoveryActionDelta = containsRecoveryAction(analysisText) ? 1 : 0;
        int relapseSignalDelta = containsRelapseSignal(analysisText) ? 1 : 0;

        int totalSignals =
                urgeLogDelta
                + betAttemptDelta
                + betBlockedDelta
                + recoveryActionDelta
                + relapseSignalDelta;

        if (totalSignals == 0) {
            return hold(
                    message,
                    currentContextExtracted
                            ? "NO_CURRENT_SUPPORTED_SIGNAL"
                            : selfContextExtracted
                                    ? "NO_SUPPORTED_SELF_SIGNAL"
                                    : "NO_SUPPORTED_SIGNAL",
                    riskPreparationMetadata
            );
        }

        double confidence = resolveConfidence(totalSignals);

        RecoveryMessageSignal signal = new RecoveryMessageSignal(
                urgeLogDelta,
                betAttemptDelta,
                betBlockedDelta,
                recoveryActionDelta,
                relapseSignalDelta,
                confidence,
                buildReason(
                        urgeLogDelta,
                        betAttemptDelta,
                        betBlockedDelta,
                        recoveryActionDelta,
                        relapseSignalDelta
                )
        );

        return new RecoveryMessageAnalysis(
                message,
                true,
                signal,
                null,
                riskPreparationMetadata
        );
    }

    private RecoveryMessageAnalysis hold(
            String message,
            String holdReason
    ) {
        return hold(
                message,
                holdReason,
                RecoveryRiskPreparationMetadata.none()
        );
    }

    private RecoveryMessageAnalysis hold(
            String message,
            String holdReason,
            RecoveryRiskPreparationMetadata riskPreparationMetadata
    ) {
        return new RecoveryMessageAnalysis(
                message,
                false,
                null,
                holdReason,
                riskPreparationMetadata
        );
    }

    private String normalize(String message) {
        if (message == null) {
            return "";
        }

        return message
                .trim()
                .replaceAll("\\s+", " ");
    }

    private boolean looksLikeThirdPartyContext(String text) {
        return containsAny(
                text,
                "친구가",
                "지인이",
                "동생이",
                "형이",
                "누나가",
                "언니가",
                "오빠가",
                "남편이",
                "아내가",
                "그 사람이"
        );
    }

    private int findExplicitSelfSubjectAfterThirdParty(String text) {
        int thirdPartyIndex = firstIndexOfAny(
                text,
                "친구가",
                "지인이",
                "동생이",
                "형이",
                "누나가",
                "언니가",
                "오빠가",
                "남편이",
                "아내가",
                "그 사람이"
        );

        if (thirdPartyIndex < 0) {
            return -1;
        }

        int selfSubjectIndex = firstSelfSubjectOutsideQuote(
                text,
                thirdPartyIndex + 1
        );

        if (selfSubjectIndex <= thirdPartyIndex) {
            return -1;
        }

        return selfSubjectIndex;
    }

    private int firstSelfSubjectOutsideQuote(
            String text,
            int startIndex
    ) {
        int firstIndex = -1;

        for (String candidate : new String[]{
                "나는",
                "내가",
                "나도"
        }) {
            int searchIndex = Math.max(0, startIndex);

            while (searchIndex < text.length()) {
                int index = text.indexOf(
                        candidate,
                        searchIndex
                );

                if (index < 0) {
                    break;
                }

                if (!isInsideQuote(text, index)) {
                    if (
                            firstIndex < 0
                            || index < firstIndex
                    ) {
                        firstIndex = index;
                    }

                    break;
                }

                searchIndex = index + candidate.length();
            }
        }

        return firstIndex;
    }

    private boolean isInsideQuote(
            String text,
            int index
    ) {
        return isInsideQuotePair(text, index, '‘', '’')
                || isInsideQuotePair(text, index, '“', '”')
                || isInsideQuotePair(text, index, '\'', '\'')
                || isInsideQuotePair(text, index, '"', '"');
    }

    private boolean isInsideQuotePair(
            String text,
            int index,
            char openingQuote,
            char closingQuote
    ) {
        int openingIndex = text.lastIndexOf(
                openingQuote,
                index
        );

        if (openingIndex < 0) {
            return false;
        }

        int closingIndex = text.indexOf(
                closingQuote,
                openingIndex + 1
        );

        return closingIndex >= index;
    }

    private int firstIndexOfAny(String text, String... candidates) {
        int firstIndex = -1;

        for (String candidate : candidates) {
            int index = text.indexOf(candidate);

            if (
                    index >= 0
                    && (
                        firstIndex < 0
                        || index < firstIndex
                    )
            ) {
                firstIndex = index;
            }
        }

        return firstIndex;
    }

    private String extractCurrentContextAfterLongPast(
            String text
    ) {
        int longPastIndex = firstIndexOfAny(
                text,
                "예전에는",
                "과거에는",
                "한때는",
                "오래전에는"
        );

        if (longPastIndex < 0) {
            return null;
        }

        int currentIndex = firstIndexOfAny(
                text,
                "이번 주에는",
                "이번주에는",
                "지금은",
                "현재는",
                "요즘은",
                "오늘은"
        );

        if (
                currentIndex < 0
                || currentIndex <= longPastIndex
        ) {
            return null;
        }

        return text.substring(currentIndex);
    }

    private boolean looksHypothetical(String text) {
        return containsAny(
                text,
                "만약",
                "가정하면",
                "그렇다면",
                "할 수도",
                "한다면",
                "했더라면"
        );
    }

    private boolean containsAffirmedUrge(String text) {
        if (containsAny(
                text,
                "충동이 없었던 건 아니",
                "충동이 없었던 것은 아니",
                "충동은 없었던 건 아니",
                "충동은 없었던 것은 아니"
        )) {
            return true;
        }

        if (containsAny(text, "충동은 없", "충동이 없", "충동 없")) {
            return false;
        }

        return containsAny(
                text,
                "충동이 왔",
                "충동이 생겼",
                "충동을 느꼈",
                "하고 싶었",
                "하고 싶은 마음",
                "베팅하고 싶",
                "결제 버튼 쪽으로 가",
                "마음이 흔들",
                "흔들렸"
        );
    }

    private boolean containsAffirmedAttempt(String text) {
        if (containsAny(
                text,
                "시도하지 않았",
                "들어가지 않았",
                "결제하지 않았",
                "시도하려던 건 아니",
                "시도하려던 것은 아니"
        )) {
            return false;
        }

        return containsAny(
                text,
                "베팅을 시도",
                "결제를 시도",
                "사이트에 들어갔",
                "베팅 사이트까지 들어갔",
                "베팅 화면을 열었",
                "결제 직전",
                "구매를 시도"
        );
    }

    private boolean containsProtectiveBlock(String text) {
        if (containsAny(
                text,
                "차단하지 않았",
                "막지 않았",
                "중단하지 않았"
        )) {
            return false;
        }

        if (
                containsFundingSelfReversal(text)
                || containsAbortedFundingSelfBlock(text)
        ) {
            return true;
        }

        return containsAny(
                text,
                "사이트를 닫",
                "앱을 닫",
                "창을 닫",
                "계정을 잠",
                "계정을 막",
                "앱을 지",
                "휴대폰을 내려놓",
                "마지막에 멈췄",
                "결제를 멈췄",
                "결제를 취소",
                "시도했지만 취소",
                "차단했",
                "차단하고",
                "막았",
                "중단했",
                "중단하고"
        );
    }

    private boolean containsRecoveryAction(String text) {
        if (containsAny(
                text,
                "상담을 요청하지 않았",
                "도움을 요청하지 않았",
                "상담을 계속 받지 않",
                "상담을 받고 있지 않"
        )) {
            return false;
        }

        if (
                containsFundingSelfReversal(text)
                || containsAbortedFundingSelfBlock(text)
        ) {
            return true;
        }

        return containsAny(
                text,
                "산책했",
                "산책을 나갔",
                "밖으로 나갔",
                "운동했",
                "전화했",
                "도움을 받았",
                "이야기했",
                "상담했",
                "상담을 요청",
                "상담을 계속 받고 있",
                "상담을 받고 있",
                "상담을 받고",
                "상담은 받고 왔",
                "도움을 요청",
                "회복 행동",
                "일기를 썼",
                "자리를 피했"
        );
    }

    private boolean containsAbortedFundingSelfBlock(
            String text
    ) {
        boolean fundingInitiated = containsAny(
                text,
                "계좌에 돈을 옮기려다가",
                "계좌로 돈을 옮기려다가",
                "이체하려다가",
                "송금하려다가",
                "충전하려다가"
        );

        boolean selfReversal = containsAny(
                text,
                "마음을 바꿔서",
                "생각을 바꿔서",
                "마음을 바꾸고",
                "생각을 바꾸고"
        );

        boolean fundingNotCompleted = containsAny(
                text,
                "이체하지 않았",
                "옮기지 않았",
                "송금하지 않았",
                "충전하지 않았",
                "돈을 넣지 않았"
        );

        return fundingInitiated
                && selfReversal
                && fundingNotCompleted;
    }

    private boolean containsRelapseSignal(String text) {
        if (containsAny(
                text,
                "재발하지 않았",
                "무너지지 않았",
                "다시 들어가지 않았"
        )) {
            return false;
        }

        if (
                containsAny(
                        text,
                        "참으려고 했",
                        "참으려 했",
                        "버티려고 했",
                        "막으려고 했",
                        "안 하려고 했",
                        "끊으려고 했",
                        "버티다가"
                )
                && containsAny(
                        text,
                        "결국 다시 들어갔",
                        "결국 또 들어갔",
                        "또다시 들어갔",
                        "다시 들어가버렸",
                        "다시 접속했",
                        "다시 접속했고",
                        "그 화면으로 돌아갔"
                )
                && containsAny(
                        text,
                        "후회",
                        "무너졌",
                        "통제하지 못했",
                        "또 해버렸"
                )
        ) {
            return true;
        }

        if (containsCompletedRelapseAfterReentry(text)) {
            return true;
        }

        if (containsBypassRelapseAfterProtectiveBlock(text)) {
            return true;
        }

        if (containsRelapseMinimizationAfterMoneyInput(text)) {
            return true;
        }

        return containsAny(
                text,
                "다시 베팅했",
                "다시 베팅한 뒤",
                "또 베팅했",
                "다시 돈을 걸었",
                "돈을 넣어버렸",
                "재발했",
                "무너졌",
                "결국 결제했",
                "통제하지 못했"
        );
    }

    private boolean containsCompletedRelapseAfterReentry(
            String text
    ) {
        boolean containsReentry = containsAny(
                text,
                "결국 다시 들어가서",
                "결국 들어가서",
                "다시 들어가서",
                "그 사이트로 돌아가서",
                "사이트로 돌아가서",
                "그 화면으로 돌아가서"
        );

        boolean containsCompletedAction = containsAny(
                text,
                "또 돈을 걸었",
                "돈을 걸었",
                "또 해버렸",
                "결국 결제했",
                "돈을 넣어버렸"
        );

        return containsReentry && containsCompletedAction;
    }

    private boolean containsBypassRelapseAfterProtectiveBlock(
            String text
    ) {
        if (containsAny(
                text,
                "돈은 넣지 않았",
                "돈을 넣지 않았",
                "돈을 넣지는 않았",
                "결제하지 않았"
        )) {
            return false;
        }

        boolean containsProtectiveBlock = containsAny(
                text,
                "계정까지 막았",
                "계정을 막았",
                "계정을 차단했",
                "계정을 잠권"
        );

        boolean containsBypassSearch = containsAny(
                text,
                "다른 곳을 찾아서",
                "다른 곳을 찾았",
                "다른 사이트를 찾아서",
                "다른 경로를 찾아서"
        );

        boolean containsCompletedMoneyInput = containsAny(
                text,
                "또 돈을 넣었",
                "돈을 넣었",
                "또 결제했",
                "결국 돈을 넣었"
        );

        return containsProtectiveBlock
                && containsBypassSearch
                && containsCompletedMoneyInput;
    }

    private boolean containsRelapseMinimizationAfterMoneyInput(
            String text
    ) {
        boolean containsCompletedMoneyInput = containsAny(
                text,
                "돈을 넣긴 했"
        );

        boolean containsRelapseMinimization = containsAny(
                text,
                "재발이라고까지는 생각하지 않"
        );

        return containsCompletedMoneyInput
                && containsRelapseMinimization;
    }

    private RecoveryRiskPreparationMetadata
            resolveRiskPreparationMetadata(String text) {

        if (containsFundingStartedThenCancelled(text)) {
            return RecoveryRiskPreparationMetadata.detected(
                    "FUNDING_STARTED_THEN_CANCELLED",
                    0.85,
                    "funding attempt was stopped and cancelled before completion"
            );
        }

        if (containsFundingCompletedWithFutureIntent(text)) {
            return RecoveryRiskPreparationMetadata.detected(
                    "FUNDING_COMPLETED_FUTURE_INTENT_PRESENT",
                    0.90,
                    "funding was completed with explicit near-future use intent"
            );
        }

        if (containsFundingCompletedWithBetNegation(text)) {
            return RecoveryRiskPreparationMetadata.detected(
                    "FUNDING_COMPLETED_BET_NEGATED",
                    0.85,
                    "funding was completed while actual betting or relapse was denied"
            );
        }

        return RecoveryRiskPreparationMetadata.none();
    }

    private boolean containsFundingCompletedWithBetNegation(
            String text
    ) {
        boolean fundingCompleted = containsAny(
                text,
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ucda9\uc804\ud574 \ub450\uc5c8",
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ucda9\uc804\ud588",
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ub123\uc5b4\ub450\uc5c8"
        );

        boolean actualBetNegated = containsAny(
                text,
                "\ubca0\ud305\uc740 \ud558\uc9c0 \uc54a\uc558",
                "\uc544\uc9c1 \uc0ac\uc6a9\ud558\uc9c0 \uc54a\uc558",
                "\uc7ac\ubc1c\uc740 \uc544\ub2c8\ub77c\uace0",
                "\uc7ac\ubc1c\uc774 \uc544\ub2c8\ub77c\uace0"
        );

        return fundingCompleted && actualBetNegated;
    }

    private boolean containsFundingCompletedWithFutureIntent(
            String text
    ) {
        boolean fundingCompleted = containsAny(
                text,
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ub123\uc5b4\ub450\uace0",
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ucda9\uc804\ud574 \ub450\uace0",
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ucda9\uc804\ud558\uace0",
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \uc62e\uaca8\ub480",
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \uc62e\uaca8\ub450\uae34 \ud588"
        );

        boolean futureIntent = containsAny(
                text,
                "\uc624\ub298 \ubc24\uc5d0 \uc0ac\uc6a9\ud560",
                "\uc774\ub530\uac00 \uc0ac\uc6a9\ud560",
                "\ub098\uc911\uc5d0 \uc0ac\uc6a9\ud560",
                "\uc4f8 \uc0dd\uac01",
                "\uc0ac\uc6a9\ud560 \uc0dd\uac01",
                "\ub2e4\uc2dc \ub4e4\uc5b4\uac00\ub824\uace0",
                "\ub2e4\uc2dc \ub4e4\uc5b4\uac08\uc9c0 \uacb0\uc815\ud560"
        );

        return fundingCompleted && futureIntent;
    }

    private boolean containsFundingSelfReversal(
            String text
    ) {
        boolean fundingCompleted = containsAny(
                text,
                "계좌에 돈을 넣어두고",
                "계좌에 돈을 충전해 두고",
                "계좌에 돈을 충전하고",
                "계좌에 돈을 옮겨뒀"
        );

        boolean gamblingContext = containsAny(
                text,
                "오늘 밤에 사용할",
                "이따가 사용할",
                "나중에 사용할",
                "쓸 생각",
                "사용할 생각",
                "다시 들어가려고",
                "다시 베팅했",
                "다시 베팅한 뒤",
                "또 베팅했",
                "다시 돈을 걸었"
        );

        boolean withdrawalCompleted = containsAny(
                text,
                "바로 다시 빼냈",
                "남은 돈을 바로 다시 빼냈",
                "다시 빼냈"
        );

        return fundingCompleted
                && gamblingContext
                && withdrawalCompleted;
    }

    private boolean containsFundingStartedThenCancelled(
            String text
    ) {
        boolean fundingStarted = containsAny(
                text,
                "\uacc4\uc88c\uc5d0 \ub3c8\uc744 \ub123\uc73c\ub824\ub2e4\uac00",
                "\ucda9\uc804\ud558\ub824\ub2e4\uac00",
                "\ucda9\uc804\uc744 \ud558\ub824\ub2e4\uac00"
        );

        boolean fundingCancelled = containsAny(
                text,
                "\ucda9\uc804\uc744 \ucde8\uc18c\ud588",
                "\ucda9\uc804\uc744 \ucde8\uc18c\ud558\uace0",
                "\ucda9\uc804 \uc804\uc5d0 \uba48\ucd84",
                "\uc911\uac04\uc5d0 \uba48\ucd94\uace0"
        );

        return fundingStarted && fundingCancelled;
    }

    private boolean containsAny(String text, String... candidates) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }
        return false;
    }

    private double resolveConfidence(int totalSignals) {
        if (totalSignals >= 3) {
            return 0.90;
        }

        if (totalSignals == 2) {
            return 0.80;
        }

        return 0.70;
    }

    private String buildReason(
            int urgeLogDelta,
            int betAttemptDelta,
            int betBlockedDelta,
            int recoveryActionDelta,
            int relapseSignalDelta
    ) {
        return "urge=%d, attempt=%d, blocked=%d, recovery=%d, relapse=%d"
                .formatted(
                        urgeLogDelta,
                        betAttemptDelta,
                        betBlockedDelta,
                        recoveryActionDelta,
                        relapseSignalDelta
                );
    }
}
