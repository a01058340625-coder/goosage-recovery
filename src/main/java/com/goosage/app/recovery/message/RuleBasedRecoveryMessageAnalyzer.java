package com.goosage.app.recovery.message;

import org.springframework.stereotype.Component;

import com.goosage.domain.recovery.message.RecoveryMessageSignal;
import com.goosage.domain.recovery.message.RecoveryPostBlockStateMetadata;
import com.goosage.domain.recovery.message.RecoveryReentryPreparationMetadata;
import com.goosage.domain.recovery.message.RecoveryReentryStateMetadata;
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

        if (
                looksLikeThirdPartyContext(normalized)
                && !containsSelfGamblingAfterFriendIntroduction(normalized)
        ) {
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

        RecoveryPostBlockStateMetadata postBlockStateMetadata =
                resolvePostBlockStateMetadata(analysisText);

        RecoveryReentryPreparationMetadata reentryPreparationMetadata =
                resolveReentryPreparationMetadata(analysisText);

        RecoveryReentryStateMetadata reentryStateMetadata =
                resolveReentryStateMetadata(analysisText);

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

        if (
                totalSignals == 0
                && !postBlockStateMetadata.detected()
                && !reentryPreparationMetadata.detected()
                && !reentryStateMetadata.detected()
        ) {
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
                riskPreparationMetadata,
                postBlockStateMetadata,
                reentryPreparationMetadata,
                reentryStateMetadata
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
                "나도",
                "저는"
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

    private int lastIndexOfAny(String text, String... candidates) {
        int lastIndex = -1;

        for (String candidate : candidates) {
            int index = text.lastIndexOf(candidate);

            if (index > lastIndex) {
                lastIndex = index;
            }
        }

        return lastIndex;
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

        int recentContextIndex = firstIndexOfAny(
                text,
                "\uc5b4\uc81c",
                "\uc624\ub298",
                "\ucd5c\uadfc",
                "\uc774\ubc88 \uc8fc",
                "\uc774\ubc88\uc8fc"
        );

        if (
                recentContextIndex >= 0
                && recentContextIndex < longPastIndex
        ) {
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

        if (containsAny(
                text,
                "충동은 없",
                "충동이 없",
                "충동 없",
                "하고 싶은 생각은 들지 않",
                "하고 싶은 생각은 전혀 들지 않"
        )) {
            return false;
        }


        if (containsCurrentGamblingGameUrge(text)) {
            return true;
        }

        if (containsMoneyDepositGamblingUrge(text)) {
            return true;
        }

        if (containsLossRecoveryGamblingUrge(text)) {
            return true;
        }

        if (containsSleepAnxietyRepeatedGambling(text)) {
            return true;
        }

        if (containsSportsBettingEscalationLossRecoveryHelpSeeking(text)) {
            return true;
        }

        if (containsAbstinenceReentryUrge(text)) {
            return true;
        }
        if (containsAbstinenceRelapseBettingEscalation(text)) {
            return true;
        }
        if (containsBigWinMemoryCasinoContinuation(text)) {
            return true;
        }
        if (containsDebtDrivenLossRecoveryRepeatedGambling(text)) {
            return true;
        }
        if (containsHabitualBettingSearchAndPersistence(text)) {
            return true;
        }
        if (containsStressTriggeredCrossGamblingCycleHelpSeeking(text)) {
            return true;
        }

        if (containsSelfGamblingAfterFriendIntroduction(text)) {
            return true;
        }

        if (
                containsGamblingSiteReentryAttempt(text)
                && containsAny(
                        text,
                        "\uc790\uc8fc \ub5a0\uc624\ub985",
                        "\uc790\uc8fc \ub5a0\uc62c\ub77c",
                        "\uacc4\uc18d \ub5a0\uc624\ub985"
                )
        ) {
            return true;
        }

        return containsAny(
                text,
                "충동이 왔",
                "충동이 생겼",
                "충동을 느꼈",
                "하고 싶었",
                "하고 싶은 마음",
                "다시 하고 싶은 생각",
                "또 풀고 싶은 생각",
                "계정을 풀고 싶은 생각",
                "또 계정을 풀고 싶은 생각",
                "베팅하고 싶",
                "결제 버튼 쪽으로 가",
                "마음이 흔들",
                "흔들렸"
        );
    }

    private boolean containsAbstinenceReentryUrge(
            String text
    ) {
        boolean abstinencePresent = containsAny(
                text,
                "\uc77c\uc8fc\uc77c \uc815\ub3c4 \uc548 \ud558\uace0",
                "\uba70\uce60\uc740 \uad1c\ucc2e\uc558"
        );

        boolean reentryUrgePresent = containsAny(
                text,
                "\uc0ac\uc774\ud2b8 \ub4e4\uc5b4\uac00\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                "\uc0ac\uc774\ud2b8\uc5d0 \ub4e4\uc5b4\uac00\uace0 \uc2f6\uc740 \ub9c8\uc74c"
        );

        boolean relapsePreventionAwareness = containsAny(
                text,
                "\ub2e4\uc2dc \ud558\uba74 \uc548 \ub41c\ub2e4",
                "\ub2e4\uc2dc \ud558\uba74 \uc548 \ub418"
        );

        return abstinencePresent
                && reentryUrgePresent
                && relapsePreventionAwareness;
    }

    private boolean containsCurrentGamblingGameUrge(
            String text
    ) {
        boolean gamblingContext = containsAny(
                text,
                "\ub3c4\ubc15",
                "\ubca0\ud305",
                "\uce74\uc9c0\ub178",
                "\uac8c\uc784",
                "\uc2ac\ub86f"
        );

        boolean currentUrge = containsAny(
                text,
                "\ub2e4\uc2dc \ud558\uace0 \uc2f6",
                "\ub610 \ud558\uace0 \uc2f6",
                "\ub354 \ud558\uace0 \uc2f6\uc5b4\uc9c0",
                "\uc0ac\uc774\ud2b8 \ub4e4\uc5b4\uac00\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                "\ud55c\ubc88\ub9cc \ud574\ubcfc\uae4c",
                "\ud55c \ubc88\ub9cc \ud574\ubcfc\uae4c"
        );

        return gamblingContext && currentUrge;
    }

    private boolean containsMoneyDepositGamblingUrge(
            String text
    ) {
        boolean gamblingContext = containsAny(
                text,
                "\ub3c4\ubc15",
                "\ubca0\ud305",
                "\uce74\uc9c0\ub178"
        );

        boolean moneyDepositContext = containsAny(
                text,
                "\uc6d4\uae09",
                "\uc785\uae08"
        );

        boolean plannedMoneyInput = containsAny(
                text,
                "\uc5bc\ub9c8\ub97c \ub123\uc744\uc9c0",
                "\uc5bc\ub9c8 \ub123\uc744\uc9c0",
                "\uc870\uae08\ub9cc \ud574\ubcfc\uae4c",
                "\uc870\uae08\ub9cc \ud558\uba74 \uba54\uc6b8 \uc218 \uc788\uc9c0 \uc54a\uc744\uae4c"
        );

        return gamblingContext
                && moneyDepositContext
                && plannedMoneyInput;
    }

    private boolean containsLossRecoveryGamblingUrge(
            String text
    ) {
        boolean gamblingContext = containsAny(
                text,
                "\ub3c4\ubc15",
                "\ubca0\ud305",
                "\uce74\uc9c0\ub178"
        );

        boolean lossRecoveryThought = containsAny(
                text,
                "\uc870\uae08\ub9cc \ud558\uba74 \uba54\uc6b8 \uc218 \uc788\uc9c0 \uc54a\uc744\uae4c",
                "\uba54\uc6b8 \uc218 \uc788\uc9c0 \uc54a\uc744\uae4c",
                "\uba54\uc6b0\uace0 \uc2f6",
                "\ub418\ucc3e\uace0 \uc2f6",
                "\ud55c \ubc88\uc5d0 \ub9cc\ud68c\ud558\ub824\ub294 \uc0dd\uac01",
                "\ud55c \ubc88\uc5d0 \ub9cc\ud68c\ud558\uace0 \uc2f6",
                "\ud55c\ubc88\uc5d0 \ub9cc\ud68c\ud558\uace0 \uc2f6"
        );

        return gamblingContext && lossRecoveryThought;
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


        if (containsGamblingSiteReentryAttempt(text)) {
            return true;
        }

        if (containsIndirectGamblingSiteSearchAttempt(text)) {
            return true;
        }

        if (containsLoginScreenEntryAttempt(text)) {
            return true;
        }

        if (containsRetryBetScreenAttempt(text)) {
            return true;
        }

        if (containsRepeatedWagerAfterSiteReentry(text)) {
            return true;
        }

        if (containsOngoingRepeatedGamblingCycle(text)) {
            return true;
        }

        if (containsGamblingEscalationAfterLossRecovery(text)) {
            return true;
        }

        if (containsSleepAnxietyRepeatedGambling(text)) {
            return true;
        }
        if (containsSportsBettingEscalationLossRecoveryHelpSeeking(text)) {
            return true;
        }
        if (containsAbstinenceRelapseBettingEscalation(text)) {
            return true;
        }
        if (containsStressTriggeredCrossGamblingCycleHelpSeeking(text)) {
            return true;
        }
        if (containsHabitualBettingSearchAndPersistence(text)) {
            return true;
        }
        if (containsDebtDrivenLossRecoveryRepeatedGambling(text)) {
            return true;
        }
        if (containsBigWinMemoryCasinoContinuation(text)) {
            return true;
        }

        if (containsSelfGamblingAfterFriendIntroduction(text)) {
            return true;
        }

        return containsAny(
                text,
                "베팅을 시도",
                "스포츠베팅을 시작했",
                "베팅 버튼을 눌렀",
                "베팅 버튼을 실제로 눌렀",
                "베팅 버튼은 눌렀",
                "베팅 버튼까지 눌렀",
                "베팅 버튼까지 실제로 눌렀",
                "결제를 시도",
                "사이트에 들어갔",
                "베팅 사이트까지 들어갔",
                "베팅 화면을 열었",
                "결제 직전",
                "구매를 시도"
        );
    }

    private boolean containsRetryBetScreenAttempt(
            String text
    ) {
        boolean retryIntent = containsAny(
                text,
                "\ub2e4\uc2dc \ud55c \ubc88 \ud574\ubcf4\ub824\uace0",
                "\ub2e4\uc2dc \ud574\ubcf4\ub824\uace0"
        );

        boolean betScreenReopened = containsAny(
                text,
                "\ubca0\ud305 \ud654\uba74\uc744 \ub2e4\uc2dc \uc5f4\uc5c8",
                "\ubca0\ud305 \ud654\uba74\uc744 \uc5f4\uc5c8"
        );

        return retryIntent && betScreenReopened;
    }

    private boolean containsRetryBetScreenSelfExit(
            String text
    ) {
        boolean attemptPresent =
                containsRetryBetScreenAttempt(text);

        boolean stoppedBeforeSecondPress = containsAny(
                text,
                "\ub450 \ubc88\uc9f8\ub85c \ub204\ub974\uae30 \uc9c1\uc804",
                "\ub204\ub974\uae30 \uc9c1\uc804"
        );

        boolean selfExit = containsAny(
                text,
                "\uc571\uc744 \uaed0",
                "\uc571\uc744 \ub2eb"
        );

        return attemptPresent
                && stoppedBeforeSecondPress
                && selfExit;
    }

    private boolean containsGamblingSiteReentryAttempt(
            String text
    ) {
        boolean gamblingSiteContext = containsAny(
                text,
                "\ub3c4\ubc15 \uc0ac\uc774\ud2b8",
                "\ubca0\ud305 \uc0ac\uc774\ud2b8",
                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8",
                "\uc608\uc804\uc5d0 \ud558\ub358 \uc0ac\uc774\ud2b8"
        );

        boolean blockedAccountFundingContext =
                containsAny(text, "\uacc4\uc815 \ub9c9\uc544\ub193", "\uacc4\uc815\uc744 \ub9c9\uc544\ub193")
                && containsAny(text, "\uc785\uae08 \ubc84\ud2bc");

        boolean reentryCompleted = containsAny(
                text,
                "\ub2e4\uc2dc \ub4e4\uc5b4\uac00\uac8c \ub410",
                "\ub2e4\uc2dc \ub4e4\uc5b4\uac00\uac8c \ub418",
                "\ub2e4\uc2dc \ucc3e\uc544\ubd24",
                "\uc8fc\uc18c\ub97c \uac80\uc0c9\ud558\uace0 \ud654\uba74\uc744",
                "\uac80\uc0c9\ud558\ub2e4\uac00 \uacb0\uad6d \ub4e4\uc5b4\uac14"
        );

        return (gamblingSiteContext || blockedAccountFundingContext)
                && reentryCompleted;
    }

    private boolean containsIndirectGamblingSiteSearchAttempt(
            String text
    ) {
        boolean siteSearch = containsAny(
                text,
                "\uad00\ub828 \uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uac80\uc0c9",
                "\uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uac80\uc0c9",
                "\uc0ac\uc774\ud2b8 \uc8fc\uc18c\uae4c\uc9c0 \uac80\uc0c9",
                "\uc0ac\uc774\ud2b8 \uc8fc\uc18c\ub97c \uac80\uc0c9",
                "\uc0ac\uc774\ud2b8 \uc8fc\uc18c\ub97c \ub2e4\uc2dc \uac80\uc0c9"
        );

        boolean typedThenDeletedSearch =
                containsAny(
                        text,
                        "\uac80\uc0c9\ucc3d\uc5d0"
                )
                && containsAny(
                        text,
                        "\uc0ac\uc774\ud2b8 \uc774\ub984"
                )
                && containsAny(
                        text,
                        "\uce58\ub824\ub2e4\uac00",
                        "\uc785\ub825\ud558\ub824\ub2e4\uac00"
                )
                && containsAny(
                        text,
                        "\uc9c0\uc6e0",
                        "\uc0ad\uc81c\ud588"
                );

        boolean gamblingAppSearchAborted =
                containsAny(
                        text,
                        "\uc2ac\ub86f",
                        "\ub3c4\ubc15",
                        "\ubca0\ud305",
                        "\uce74\uc9c0\ub178"
                )
                && containsAny(
                        text,
                        "\uc571\uc744 \ucc3e\ub2e4\uac00",
                        "\uc571\uc744 \ucc3e\uc558"
                )
                && containsAny(
                        text,
                        "\uc124\uce58\ub294 \uc548",
                        "\uc124\uce58\ud558\uc9c0 \uc54a"
                );

        boolean gamblingBoundaryContext = containsAny(
                text,
                "\ub3c8\uc744 \uac78\uc9c0\ub294 \uc54a\uc558",
                "\ub3c8\uc744 \uac78\uc9c0 \uc54a\uc558",
                "\ubca0\ud305\uc740 \ud558\uc9c0 \uc54a\uc558",
                "\uc811\uc18d \ubc84\ud2bc\uc740 \ub204\ub974\uc9c0 \uc54a\uc558",
                "\uc544\uc9c1 \ub85c\uadf8\uc778\ud558\uac70\ub098 \ub3c8\uc744 \ub123\uc9c0\ub294 \uc54a\uc558"
        );

        return (siteSearch && gamblingBoundaryContext)
                || typedThenDeletedSearch
                || gamblingAppSearchAborted;
    }

    private boolean containsLoginScreenEntryAttempt(
            String text
    ) {
        boolean accountContext = containsAny(
                text,
                "\uacc4\uc815",
                "\uc0ac\uc774\ud2b8"
        );

        boolean loginScreenEntered = containsAny(
                text,
                "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac00",
                "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac14",
                "\ub85c\uadf8\uc778 \ud654\uba74\uc5d0 \ub4e4\uc5b4\uac00",
                "\ub85c\uadf8\uc778 \ud654\uba74\uc5d0 \ub4e4\uc5b4\uac14"
        );

        boolean loginNotCompleted = containsAny(
                text,
                "\uc544\uc774\ub514\ub791 \ube44\ubc00\ubc88\ud638\ub294 \uc785\ub825\ud558\uc9c0 \uc54a\uc558",
                "\uc544\uc774\ub514\uc640 \ube44\ubc00\ubc88\ud638\ub294 \uc785\ub825\ud558\uc9c0 \uc54a\uc558",
                "\uc544\uc774\ub514\ub098 \ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\uac70\ub098 \ub3c8\uc744 \ub123\uc9c0\ub294 \uc54a",
                "\ub85c\uadf8\uc778\ud558\uc9c0 \uc54a\uc558"
        );

        return accountContext
                && loginScreenEntered
                && loginNotCompleted;
    }

    private boolean containsProtectiveBlock(String text) {
        if (
                containsAny(
                        text,
                        "계정을 막을지",
                        "계정을 차단할지",
                        "계정을 잠글지",
                        "계정을 막을까",
                        "계정을 차단할까"
                )
                && containsAny(
                        text,
                        "생각해볼",
                        "고민 중",
                        "고민하고",
                        "결정할"
                )
        ) {
            return false;
        }

        if (
                containsUnblockRequestSubmittedThenCancelled(text)
                || containsUnblockFinalConfirmationSubmitted(text)
        ) {
            return true;
        }

    boolean reentrySelfExitBeforeWager =
                containsReentrySelfExitBeforeWager(text);

        if (
                containsAny(
                        text,
                        "???? ??",
                        "?? ??",
                        "???? ??"
                )
                && !reentrySelfExitBeforeWager
        ) {
            return false;
        }

        if (
                containsFundingSelfReversal(text)
                || containsAbortedFundingSelfBlock(text)
                || containsAnxietyStoppedFundingBlock(text)
                || reentrySelfExitBeforeWager
                || containsRetryBetScreenSelfExit(text)
        ) {
            return true;
        }

        return containsAny(
                text,
                "사이트를 닫",
                "앱을 닫",
                "창을 닫",
                "\uc2a4\uc2a4\ub85c \ud654\uba74\uc744 \ub2eb",
                "계정을 잠",
                "계정을 막",
                "계정은 이미 막아놓",
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

    private boolean containsUnblockFinalConfirmationSubmitted(
            String text
    ) {
        boolean finalConfirmationPressed = containsAny(
                text,
                "\ub9c8\uc9c0\ub9c9 \ud655\uc778\uae4c\uc9c0 \ub20c\ub800",
                "\ub9c8\uc9c0\ub9c9 \ucd5c\uc885 \ud655\uc778\uae4c\uc9c0 \ub20c\ub800",
                "\ub9c8\uc9c0\ub9c9 \ud655\uc778\uc744 \ub20c\ub800",
                "\ucd5c\uc885 \ud655\uc778\uae4c\uc9c0 \ud588",
                "\ucd5c\uc885 \ud655\uc778\uc744 \ud588"
        );

        boolean actualUnblockNotCompleted = containsAny(
                text,
                "\uc544\uc9c1 \uacc4\uc815\uc774 \uc2e4\uc81c\ub85c "
                        + "\ud574\uc81c\ub418\uc9c0\ub294 \uc54a",
                "\uc544\uc9c1 \uacc4\uc815\uc740 \ud574\uc81c\ub418\uc9c0 \uc54a"
,
            "\uc544\uc9c1 \uacc4\uc815\uc740 \uc2e4\uc81c\ub85c \ud574\uc81c\ub418\uc9c0 \uc54a"
        );

        return finalConfirmationPressed
                && actualUnblockNotCompleted;
    }
    private boolean containsUnblockRequestSubmittedBeforeFinalConfirmation(
            String text
    ) {
        boolean requestSubmitted = containsAny(
                text,
                "\uc81c\ucd9c \ubc84\ud2bc\uae4c\uc9c0 \ub20c\ub800",
                "\uc81c\ucd9c \ubc84\ud2bc\uc744 \ub20c\ub800",
                "\ud574\uc81c \uc694\uccad \ubc84\ud2bc\uae4c\uc9c0 \ub20c\ub800",
                "\ud574\uc81c \uc694\uccad \ubc84\ud2bc\uc744 \ub20c\ub800"
,
                "\ud574\uc81c \uc2e0\uccad\uc11c\ub97c \ub2e4\uc2dc \uc81c\ucd9c\ud588",
                "\ud574\uc81c \uc694\uccad\ub3c4 \uc2e4\ud589\ud588"
        );

        boolean finalConfirmationNotCompleted = containsAny(
                text,
                "\uc544\uc9c1 \ucd5c\uc885 \ud655\uc778\uc740 \ud558\uc9c0 \uc54a",
                "\ucd5c\uc885 \ud655\uc778\uc740 \ud558\uc9c0 \uc54a",
                "\ucd5c\uc885 \ud655\uc778 \uc804",
                "\ub9c8\uc9c0\ub9c9 \ud655\uc778 \uc804"
,
                "\ub9c8\uc9c0\ub9c9 \ucd5c\uc885 \ud655\uc778\uc740 \ub204\ub974\uc9c0 \uc54a"
        );

        boolean actualUnblockNotCompleted = containsAny(
                text,
                "\uacc4\uc815\ub3c4 \uc2e4\uc81c\ub85c \ud480\ub9ac\uc9c0\ub294 \uc54a",
                "\uacc4\uc815\uc774 \uc2e4\uc81c\ub85c \ud574\uc81c\ub418\uc9c0 \uc54a"
,
            "\ub9c8\uc9c0\ub9c9 \ucd5c\uc885 \ud655\uc778\uc740 \ub204\ub974\uc9c0 \uc54a"
        );

        return requestSubmitted
                && finalConfirmationNotCompleted
                && actualUnblockNotCompleted;
    }

    private boolean containsUnblockRequestSubmittedThenCancelled(
            String text
    ) {
        boolean requestButtonPressed = containsAny(
                text,
                "\ud574\uc81c \uc694\uccad \ubc84\ud2bc\uae4c\uc9c0 \ub20c\ub800",
                "\ud574\uc81c \uc694\uccad \ubc84\ud2bc\uc744 \ub20c\ub800"
        );

        boolean finalConfirmationCancelled = containsAny(
                text,
                "\ub9c8\uc9c0\ub9c9 \ud655\uc778 \ud654\uba74\uc5d0\uc11c \ucde8\uc18c",
                "\ub9c8\uc9c0\ub9c9 \ud655\uc778 \ub2e8\uacc4\uc5d0\uc11c \ucde8\uc18c",
                "\uc2e4\uc81c \ud574\uc81c \uc694\uccad\uc740 \uc644\ub8cc\ud558\uc9c0 \uc54a"
        );

        return requestButtonPressed
                && finalConfirmationCancelled;
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

        if (containsSportsBettingEscalationLossRecoveryHelpSeeking(text)) {
            return true;
        }

        if (containsStressTriggeredCrossGamblingCycleHelpSeeking(text)) {
            return true;
        }

        boolean recoveryPhoneCall =
                containsAny(text, "전화했")
                && !containsAny(text, "고객센터");

        return recoveryPhoneCall
                || containsAny(
                text,
                "산책했",
                "산책을 나갔",
                "밖으로 나갔",
                "운동했",
                "도움을 받았",
                "이야기했",
                "상담했",
                "상담을 요청",
                "상담을 계속 받고 있",
                "상담을 받고 있",
                "상담을 받고",
                "상담은 받고 왔",
                "\uc0c1\ub2f4\uc744 \ubc1b\uc544\ubcf4\ub824\uace0 \uae00 \ub0a8",
                "\uc0c1\ub2f4\uc744 \ubc1b\uc544\uc57c \ud560 \uac83 \uac19\uc544 \uae00\uc744 \ub0a8",
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

    private boolean containsAnxietyStoppedFundingBlock(
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

        boolean anxietyTriggered = containsAny(
                text,
                "불안해져서",
                "불안해서",
                "겁이 나서",
                "무서워져서"
        );

        boolean fundingStopped = containsAny(
                text,
                "이체를 멈췄",
                "이체를 중단했",
                "송금을 멈췄",
                "송금을 중단했",
                "충전을 멈췄",
                "충전을 중단했"
        );

        return fundingInitiated
                && anxietyTriggered
                && fundingStopped;
    }

    private boolean containsReentrySelfExitBeforeWager(
            String text
    ) {
        boolean siteReentryCompleted = containsAny(
                text,
                "사이트에 들어가긴 했지만",
                "사이트에 들어갔지만",
                "사이트에 들어갔다가",
                "사이트로 들어가긴 했지만",
                "사이트로 들어갔지만",
                "사이트로 들어갔다가"
        );

        boolean wagerImminent = containsAny(
                text,
                "돈을 걸기 직전에",
                "베팅하기 직전에",
                "베팅을 하기 직전에",
                "결제하기 직전에"
        );

        boolean selfExitTriggered = containsAny(
                text,
                "무서워져서 그냥 나왔",
                "무서워져서 나왔",
                "무서워서 그냥 나왔",
                "무서워서 나왔",
                "겁이 나서 그냥 나왔",
                "겁이 나서 나왔",
                "불안해져서 그냥 나왔",
                "불안해져서 나왔",
                "스스로 나왔",
                "그냥 나왔"
        );

        boolean externalInterruption = containsAny(
                text,
                "가족이 휴대폰을 가져가",
                "휴대폰을 빼앗",
                "오류가 나서",
                "접속이 끊겨",
                "시간이 없어서",
                "강제로 나가"
        );

        boolean wagerCompleted = containsAny(
                text,
                "돈을 걸었",
                "베팅했",
                "결제했"
        );

        return siteReentryCompleted
                && wagerImminent
                && selfExitTriggered
                && !externalInterruption
                && !wagerCompleted;
    }

    private boolean containsReentrySelfExitWithRetryIntent(
            String text
    ) {
        boolean retryIntent = containsAny(
                text,
                "조금 진정되면 다시 들어갈 생각",
                "진정되면 다시 들어갈 생각",
                "조금 후에 다시 들어갈 생각",
                "나중에 다시 들어갈 생각",
                "다시 들어갈 생각이야",
                "다시 들어갈 생각이 있어",
                "다시 접속할 생각"
        );

        boolean retryNegated = containsAny(
                text,
                "다시 들어갈 생각은 없어",
                "다시는 들어가지 않을",
                "다시 접속할 생각은 없어",
                "이제 다시 하지 않을",
                "다시 들어가지 않기로"
        );

        return containsReentrySelfExitBeforeWager(text)
                && retryIntent
                && !retryNegated;
    }

    private boolean containsRelapseSignal(String text) {
        if (containsAny(
                text,
                "재발하지 않았",
                "무너지지 않았",
                "다시 들어가지 않았",
                "돈을 걸지 않았",
                "돈은 걸지 않았",
                "베팅하지 않았",
                "베팅은 하지 않았"
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

        if (containsMoneyTriggeredRepeatedRelapse(text)) {
            return true;
        }

        if (containsGamblingRestartByReengagement(text)) {
            return true;
        }

        if (containsGamblingRestartCompleted(text)) {
            return true;
        }

        if (containsCompletedWagerWithNormalOrder(text)) {
            return true;
        }

        if (containsRepeatedWagerAfterSiteReentry(text)) {
            return true;
        }

        if (containsOngoingRepeatedGamblingCycle(text)) {
            return true;
        }

        if (containsGamblingEscalationAfterLossRecovery(text)) {
            return true;
        }

        if (containsSleepAnxietyRepeatedGambling(text)) {
            return true;
        }
        if (containsSportsBettingEscalationLossRecoveryHelpSeeking(text)) {
            return true;
        }
        if (containsAbstinenceRelapseBettingEscalation(text)) {
            return true;
        }
        if (containsStressTriggeredCrossGamblingCycleHelpSeeking(text)) {
            return true;
        }
        if (containsHabitualBettingSearchAndPersistence(text)) {
            return true;
        }
        if (containsDebtDrivenLossRecoveryRepeatedGambling(text)) {
            return true;
        }
        if (containsBigWinMemoryCasinoContinuation(text)) {
            return true;
        }

        if (containsSelfGamblingAfterFriendIntroduction(text)) {
            return true;
        }

        return containsAny(
                text,
                "다시 베팅했",
                "실제로 베팅했",
                "실제 베팅이 성립됐",
                "실제 베팅이 성립된 뒤",
                "다시 베팅이 성립된 뒤",
                "실제 베팅이 한 번 성립된 뒤",
                "실제 베팅까지 한 번 성립됐",
                "베팅이 실제로 성립된",
                "베팅 버튼을 눌렀고 주문까지 정상 처리됐",
                "다시 베팅한 뒤",
                "또 베팅했",
                "또 베팅하게 돼",
                "다시 도박을 시작하게 돼",
                "도박을 시작했",
                "다시 돈을 걸었",
                "돈을 걸었",
                "돈을 넣어버렸",
                "베팅을 해버렸",
                "재발했",
                "무너졌",
                "결국 결제했",
                "통제하지 못했"
        );
    }

    private boolean containsRepeatedWagerAfterSiteReentry(
            String text
    ) {
        boolean siteReentry = containsAny(
                text,
                "\uc0ac\uc774\ud2b8\uc5d0 \ub2e4\uc2dc \ub4e4\uc5b4\uac00",
                "\uc0ac\uc774\ud2b8\ub85c \ub2e4\uc2dc \ub4e4\uc5b4\uac00"
        );

        boolean repeatedWagerCompleted = containsAny(
                text,
                "\ub610 \ud55c \ubc88 \ubca0\ud305\ud588",
                "\ub610 \ud55c\ubc88 \ubca0\ud305\ud588"
        );

        return siteReentry && repeatedWagerCompleted;
    }

    private boolean containsCompletedWagerWithNormalOrder(
            String text
    ) {
        boolean wagerCompleted = containsAny(
                text,
                "\ubca0\ud305\ud588\uace0",
                "\ud55c \ubc88 \ubca0\ud305\ud588\uace0"
        );

        boolean orderCompleted = containsAny(
                text,
                "\uc8fc\ubb38\ub3c4 \uc815\uc0c1\uc801\uc73c\ub85c \ucc98\ub9ac\ub410",
                "\uc8fc\ubb38\uc774 \uc815\uc0c1\uc801\uc73c\ub85c \ucc98\ub9ac\ub410",
                "\uc8fc\ubb38\ub3c4 \uc815\uc0c1 \ucc98\ub9ac\ub410"
        );

        return wagerCompleted && orderCompleted;
    }

    private boolean containsCompletedRelapseAfterReentry(
            String text
    ) {
        boolean containsReentry = containsAny(
                text,
                "결국 다시 들어가서",
                "결국 들어가서",
                "다시 들어가서",
                "사이트에 들어가서",
                "사이트로 들어가서",
                "그 사이트에 들어가서",
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

    private boolean containsSleepAnxietyRepeatedGambling(
            String text
    ) {
        boolean sleepTrigger = containsAny(
                text,
                "\ubc24\uc5d0 \uc7a0\uc774 \uc548 \uc640",
                "\uc7a0\uc774 \uc548 \uc640\uc11c"
        );

        boolean anxietyWithoutGambling = containsAny(
                text,
                "\uc548 \ud558\uba74 \ub354 \ubd88\uc548",
                "\uc548 \ud558\uba74 \ubd88\uc548"
        );

        boolean repeatedOutcomeChecking = containsAny(
                text,
                "\ud558\ub8e8 \uc885\uc77c \uacb0\uacfc \ud655\uc778",
                "\uacb0\uacfc \ud655\uc778\ud558\uace0"
        );

        boolean concealment = containsAny(
                text,
                "\uac00\uc871\ud55c\ud14c\ub294 \uadf8\ub0e5 \uac8c\uc784",
                "\uac00\uc871\uc5d0\uac8c\ub294 \uadf8\ub0e5 \uac8c\uc784"
        );

        boolean moneyInput = containsAny(
                text,
                "\ub3c8\ub3c4 \uaf64 \ub9ce\uc774 \ub4e4\uc5b4\uac14",
                "\ub3c8\uc774 \uaf64 \ub9ce\uc774 \ub4e4\uc5b4\uac14"
        );

        boolean siteOpened = containsAny(
                text,
                "\uc0ac\uc774\ud2b8\ub97c \ucf1c\uac8c \ub418",
                "\uc0ac\uc774\ud2b8\ub97c \ucf30"
        );

        return sleepTrigger
                && anxietyWithoutGambling
                && repeatedOutcomeChecking
                && concealment
                && moneyInput
                && siteOpened;
    }

    private boolean containsGamblingEscalationAfterLossRecovery(
            String text
    ) {
        boolean gamblingContext = containsAny(
                text,
                "\uc628\ub77c\uc778 \uce74\uc9c0\ub178",
                "\uc2ac\ub86f",
                "\uc2a4\ud3ec\uce20\ubca0\ud305"
        );

        boolean lossRecoveryUrge = containsAny(
                text,
                "\uc783\uc740 \ub3c8\uc744 \ub418\ucc3e\uace0 \uc2f6",
                "\ub418\ucc3e\uace0 \uc2f6\ub2e4\ub294 \ub9c8\uc74c",
                "\ud55c \ubc88\uc5d0 \ub9cc\ud68c\ud558\ub824\ub294 \uc0dd\uac01"
        );

        boolean gamblingExpansion = containsAny(
                text,
                "\uc2a4\ud3ec\uce20\ubca0\ud305\uae4c\uc9c0 \uc190\ub300",
                "\ubca0\ud305\uc561\uc774 \ucee4\uc84c",
                "\ubca0\ud305 \uae08\uc561\uc774 \ucee4\uc84c",
                "\uc628\ub77c\uc778 \uce74\uc9c0\ub178\uc640 \uc2ac\ub86f\uae4c\uc9c0 \uac19\uc774 \ud558\uac8c \ub410"
        );

        return gamblingContext
                && lossRecoveryUrge
                && gamblingExpansion;
    }

    private boolean containsOngoingRepeatedGamblingCycle(
            String text
    ) {
        boolean gamblingContext = containsAny(
                text,
                "\ub3c4\ubc15",
                "\ubca0\ud305",
                "\uce74\uc9c0\ub178"
        );

        boolean ongoingGambling = containsAny(
                text,
                "\uc544\uc9c1 \ub3c4\ubc15\uc744 \ud558\uace0 \uc788\ub294 \uc0c1\ud0dc",
                "\ub3c4\ubc15\uc744 \ud558\uace0 \uc788\ub294 \uc0c1\ud0dc"
        );

        boolean repeatedEntry = containsAny(
                text,
                "\ub610 \ub4e4\uc5b4\uac00\uac8c \ub418",
                "\ub610 \ub4e4\uc5b4\uac00\uac8c \ub418\ub124"
        );

        boolean repeatedLoss = containsAny(
                text,
                "\ub610 \uc783\uace0 \ubc18\ubcf5",
                "\ub610 \uc783\uace0"
        );

        return gamblingContext
                && ongoingGambling
                && repeatedEntry
                && repeatedLoss;
    }

    private boolean containsGamblingRestartCompleted(
            String text
    ) {
        boolean gamblingContext = containsAny(
                text,
                "\ub3c4\ubc15",
                "\ubca0\ud305",
                "\uce74\uc9c0\ub178"
        );

        boolean restartCompleted = containsAny(
                text,
                "\ub2e4\uc2dc \uc2dc\uc791\ud588",
                "\ub2e4\uc2dc \uc2dc\uc791\ud588\ub2e4\uac00"
        );

        return gamblingContext && restartCompleted;
    }

    private boolean containsGamblingRestartByReengagement(
            String text
    ) {
        boolean gamblingContext = containsAny(
                text,
                "\ub3c4\ubc15",
                "\ubca0\ud305",
                "\uce74\uc9c0\ub178"
        );

        boolean restartAction = containsAny(
                text,
                "\uc190\uc744 \ub300\uae30 \uc2dc\uc791",
                "\uc190\ub300\uae30 \uc2dc\uc791"
        );

        return gamblingContext && restartAction;
    }

    private boolean containsMoneyTriggeredRepeatedRelapse(
            String text
    ) {
        boolean moneyTrigger = containsAny(
                text,
                "\ub3c8\ub9cc \uc0dd\uae30\uba74",
                "\ub3c8\uc774 \uc0dd\uae30\uba74"
        );

        boolean repeatedReturn = containsAny(
                text,
                "\ub2e4\uc2dc \ud558\uac8c \ub3fc",
                "\ub2e4\uc2dc \ud558\uac8c \ub418"
        );

        return moneyTrigger && repeatedReturn;
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

    private boolean isSelfExitLaterThanProtectiveBlock(
            String text
    ) {
        int protectiveBlockIndex = lastIndexOfAny(
                text,
                "\uacc4\uc815\uc744 \ub9c9\uc558",
                "\uacc4\uc815\uc744 \ucc28\ub2e8\ud588",
                "\uacc4\uc815\uc744 \ub2e4\uc2dc \ucc28\ub2e8\ud588",
                "\uacc4\uc815\uc744 \uc7a0\uac00",
                "\uacc4\uc815\uc744 \ub9c9\uace0",
                "\uacc4\uc815\uc744 \ucc28\ub2e8\ud558\uace0"
        );

        int selfExitIndex = lastIndexOfAny(
                text,
                "\ubb34\uc11c\uc6cc\uc838\uc11c \uadf8\ub0e5 \ub098\uc654",
                "\ubb34\uc11c\uc6cc\uc838\uc11c \ub098\uc654",
                "\ubb34\uc11c\uc6cc\uc11c \uadf8\ub0e5 \ub098\uc654",
                "\ubb34\uc11c\uc6cc\uc11c \ub098\uc654",
                "\uac81\uc774 \ub098\uc11c \uadf8\ub0e5 \ub098\uc654",
                "\uac81\uc774 \ub098\uc11c \ub098\uc654",
                "\ubd88\uc548\ud574\uc838\uc11c \uadf8\ub0e5 \ub098\uc654",
                "\ubd88\uc548\ud574\uc838\uc11c \ub098\uc654",
                "\uc2a4\uc2a4\ub85c \ub098\uc654",
                "\uadf8\ub0e5 \ub098\uc654"
        );

        return protectiveBlockIndex >= 0
                && selfExitIndex > protectiveBlockIndex;
    }

    private RecoveryPostBlockStateMetadata
            resolvePostBlockStateMetadata(String text) {

        if (
                containsActualUnblockCompleted(text)
                || containsPostBlockReentryLoginCompleted(text)
        ) {
            return RecoveryPostBlockStateMetadata.detected(
                    "PROTECTIVE_BLOCK_REVERSAL_COMPLETED",
                    0.90,
                    "protective gambling account block was actually removed "
                    + "without confirmed site reentry or wagering"
            );
        }

        return RecoveryPostBlockStateMetadata.none();
    }

    private boolean containsActualUnblockCompleted(String text) {
        boolean unblockCompleted = containsAny(
                text,
                "계정 차단을 실제로 해제했",
                "계정 차단을 해제했",
                "차단된 계정을 실제로 해제했",
                "계정 차단은 이미 해제했",
                "차단을 다시 해제했고",
                "\uacc4\uc815 \ucc28\ub2e8\uc740 \ud574\uc81c\ud588\uace0",
                "\uacc4\uc815 \ucc28\ub2e8\uc774 \uc2e4\uc81c\ub85c \ud574\uc81c\ub410",
                "\uc2e4\uc81c\ub85c \uacc4\uc815 \ucc28\ub2e8\ub3c4 \ud574\uc81c\ub410",
                "\uc2e4\uc81c\ub85c \uacc4\uc815 \ucc28\ub2e8\ub3c4 \ud574\uc81c\ud588",
                "\uc2e4\uc81c\ub85c \ucc28\ub2e8 \ud574\uc81c\uae4c\uc9c0 \uc644\ub8cc\ud588"
        );

        boolean reentryOrWagerCompleted = containsAny(
                text,
                "사이트에 다시 들어갔",
                "사이트에 접속했",
                "돈을 걸었",
                "베팅했",
                "\uc2e4\uc81c\ub85c \ub85c\uadf8\uc778\ud55c"
        );

        return unblockCompleted
                && !reentryOrWagerCompleted;
    }

    private RecoveryReentryPreparationMetadata
            resolveReentryPreparationMetadata(String text) {

        if (containsPostBlockReentryInterfaceReached(text)) {
            return RecoveryReentryPreparationMetadata.detected(
                    "POST_BLOCK_REENTRY_INTERFACE_REACHED",
                    0.90,
                    "account unblock was completed and the user reached "
                    + "the gambling site login interface without logging in or wagering"
            );
        }

        return RecoveryReentryPreparationMetadata.none();
    }

    private boolean containsPostBlockReentryInterfaceReached(
            String text
    ) {
        boolean unblockCompleted = containsAny(
                text,
                "계정 차단은 이미 해제했고",
                "계정 차단을 이미 해제했고",
                "계정 차단을 해제했고",
                "차단을 다시 해제했고",
                "\uacc4\uc815 \ucc28\ub2e8\uc740 \ud574\uc81c\ud588\uace0",
                "\uc2e4\uc81c\ub85c \uacc4\uc815 \ucc28\ub2e8\ub3c4 \ud574\uc81c\ud588",
                "\uc2e4\uc81c\ub85c \ucc28\ub2e8 \ud574\uc81c\uae4c\uc9c0 \uc644\ub8cc\ud588"
        );

        boolean reentryInterfaceReached = containsAny(
                text,
                "사이트 로그인 화면까지 들어갔",
                "로그인 화면까지 들어갔",
                "사이트 로그인 화면을 열었"
        );

        boolean loginOrWagerCompleted = containsAny(
                text,
                "실제로 로그인했",
                "로그인해서",
                "돈을 걸었",
                "베팅했"
        );

        return unblockCompleted
                && reentryInterfaceReached
                && !loginOrWagerCompleted;
    }

    private RecoveryReentryStateMetadata
            resolveReentryStateMetadata(String text) {

        if (containsAny(
                text,
                "\uc2e4\uc81c \ubca0\ud305\uc774 \uc131\ub9bd\ub410",
                "\ubca0\ud305\uc774 \uc131\ub9bd\ub410",
                "\uc8fc\ubb38\uae4c\uc9c0 \uc815\uc0c1 \ucc98\ub9ac\ub410"
        )) {
            return RecoveryReentryStateMetadata.none();
        }

        if (containsPostBlockWagerAttemptFailed(text)) {
            return RecoveryReentryStateMetadata.detected(
                    "POST_BLOCK_WAGER_ATTEMPT_FAILED",
                    0.96,
                    "funding was completed and the user attempted a wager "
                    + "but the wager order failed before completion"
            );
        }

        if (containsPostBlockReentryFundingCompleted(text)) {
            return RecoveryReentryStateMetadata.detected(
                    "POST_BLOCK_REENTRY_FUNDING_COMPLETED",
                    0.94,
                    "account unblock and gambling-site login were completed "
                    + "and the user completed funding before wagering"
            );
        }

        if (containsPostBlockReentryLoginCompleted(text)) {
            return RecoveryReentryStateMetadata.detected(
                    "POST_BLOCK_REENTRY_LOGIN_COMPLETED",
                    0.92,
                    "account unblock was completed and the user "
                    + "completed gambling-site login"
            );
        }

        if (containsGeneralReentryWagerAttemptFailed(text)) {
            return RecoveryReentryStateMetadata.detected(
                    "REENTRY_WAGER_ATTEMPT_FAILED",
                    0.94,
                    "the user funded a gambling-related session and attempted "
                    + "a wager, but the wager failed before completion"
            );
        }

        if (containsGeneralReentryFundingCompleted(text)) {
            return RecoveryReentryStateMetadata.detected(
                    "REENTRY_FUNDING_COMPLETED",
                    0.92,
                    "the user completed login and moved money into a previously "
                    + "used gambling-related site before wagering"
            );
        }

        if (containsGeneralReentryLoginCompleted(text)) {
            return RecoveryReentryStateMetadata.detected(
                    "REENTRY_LOGIN_COMPLETED",
                    0.90,
                    "the user completed login to a previously used "
                    + "gambling-related site without confirmed funding or wagering"
            );
        }

        return RecoveryReentryStateMetadata.none();
    }

    private boolean containsGeneralReentryWagerAttemptFailed(
            String text
    ) {
        boolean fundingCompleted = containsAny(
                text,
                "\ub3c8\uae4c\uc9c0 \ub123\uc5b4\ub193",
                "\ub3c8\uc744 \ub123\uc5b4\ub193",
                "\ub3c8\uc744 \ub123\uc5c8",
                "\uc785\uae08\ud588"
        );

        boolean wagerAttempted = containsAny(
                text,
                "\ubca0\ud305 \ubc84\ud2bc\uc744 \ub20c\ub800",
                "\ubca0\ud305 \ubc84\ud2bc\uae4c\uc9c0 \ub20c\ub800"
        );

        boolean orderFailed = containsAny(
                text,
                "\uc8fc\ubb38 \uc624\ub958",
                "\uc8fc\ubb38\uc774 \uc2e4\ud328",
                "\uc8fc\ubb38 \uc2e4\ud328"
        );

        boolean wagerNotCompleted = containsAny(
                text,
                "\ubca0\ud305\uc740 \uc131\ub9bd\ub418\uc9c0 \uc54a",
                "\ubca0\ud305\uc774 \uc131\ub9bd\ub418\uc9c0 \uc54a"
        );

        return fundingCompleted
                && wagerAttempted
                && orderFailed
                && wagerNotCompleted;
    }

    private boolean containsGeneralReentryFundingCompleted(
            String text
    ) {
        boolean priorSiteContext = containsAny(
                text,
                "\uc608\uc804\uc5d0 \uc4f0\ub358 \uc0ac\uc774\ud2b8",
                "\uc608\uc804\uc5d0 \ud558\ub358 \uc0ac\uc774\ud2b8",
                "\ub3c4\ubc15 \uc0ac\uc774\ud2b8",
                "\ubca0\ud305 \uc0ac\uc774\ud2b8",
                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8"
        );

        boolean loginCompleted = containsAny(
                text,
                "\ub85c\uadf8\uc778\ud558\uace0",
                "\ub85c\uadf8\uc778\ud588\uace0",
                "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588"
        );

        boolean fundingCompleted = containsAny(
                text,
                "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub194",
                "\ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub194",
                "\ub3c8\uc744 \uc62e\uaca8\ub194",
                "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub1a8",
                "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub1a8\uc5b4",
                "\ub3c8\uc744 \ub123\uc5c8",
                "\uc785\uae08\ud588"
        );

        boolean wagerCompleted = containsAny(
                text,
                "\ubca0\ud305\ud588",
                "\ub3c8\uc744 \uac78\uc5c8"
        );

        return priorSiteContext
                && loginCompleted
                && fundingCompleted
                && !wagerCompleted;
    }

    private boolean containsGeneralReentryLoginCompleted(
            String text
    ) {
        boolean priorSiteContext = containsAny(
                text,
                "\uc608\uc804\uc5d0 \uc4f0\ub358 \uc0ac\uc774\ud2b8",
                "\uc608\uc804\uc5d0 \ud558\ub358 \uc0ac\uc774\ud2b8",
                "\ub3c4\ubc15 \uc0ac\uc774\ud2b8",
                "\ubca0\ud305 \uc0ac\uc774\ud2b8",
                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8"
        );

        boolean blockedAccountFundingContext =
                containsAny(text, "\uacc4\uc815 \ub9c9\uc544\ub193", "\uacc4\uc815\uc744 \ub9c9\uc544\ub193")
                && containsAny(text, "\uc785\uae08 \ubc84\ud2bc");

        boolean loginCompleted = containsAny(
                text,
                "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588",
                "\ub85c\uadf8\uc778\ud588\uace0",
                "\ub85c\uadf8\uc778\ud574\uc11c",
                "\ub85c\uadf8\uc778\ud588\uc5b4\uc694",
                "\ub610 \ub85c\uadf8\uc778\ud588"
        );

        boolean fundingOrWagerCompleted = containsAny(
                text,
                "\ub3c8\uc744 \ub123\uc5c8",
                "\uc785\uae08\ud588",
                "\ub3c8\uc744 \uac78\uc5c8",
                "\ubca0\ud305\ud588"
        );

        return (priorSiteContext || blockedAccountFundingContext)
                && loginCompleted
                && !fundingOrWagerCompleted;
    }

    private boolean containsPostBlockWagerAttemptFailed(
            String text
    ) {
        boolean fundingCompleted =
                containsPostBlockReentryFundingCompleted(text);

        boolean wagerAttempted = containsAny(
                text,
                "\ubca0\ud305 \ubc84\ud2bc\uae4c\uc9c0 \ub20c\ub800",
                "\ubca0\ud305 \ubc84\ud2bc\uae4c\uc9c0 \uc2e4\uc81c\ub85c \ub20c\ub800",
                "\ubca0\ud305 \ubc84\ud2bc\uc744 \ub20c\ub800"
        );

        boolean orderFailed = containsAny(
                text,
                "\uc8fc\ubb38\uc774 \ucc98\ub9ac\ub418\uc9c0 \uc54a",
                "\uc8fc\ubb38\uc774 \uc2e4\ud328",
                "\uc8fc\ubb38 \uc2e4\ud328"
        );

        boolean wagerNotCompleted = containsAny(
                text,
                "\ubca0\ud305\uc740 \uc131\ub9bd\ub418\uc9c0 \uc54a",
                "\ubca0\ud305\uc774 \uc131\ub9bd\ub418\uc9c0 \uc54a"
        );

        boolean wagerCompleted = containsAny(
                text,
                "\uc2e4\uc81c \ubca0\ud305\uc774 \uc131\ub9bd\ub410",
                "\ubca0\ud305\uc774 \uc131\ub9bd\ub410",
                "\uc8fc\ubb38\uae4c\uc9c0 \uc815\uc0c1 \ucc98\ub9ac\ub410"
        );

        return fundingCompleted
                && wagerAttempted
                && orderFailed
                && !wagerCompleted;
    }

    private boolean containsPostBlockReentryFundingCompleted(
            String text
    ) {
        boolean loginCompleted =
                containsPostBlockReentryLoginCompleted(text);

        boolean fundingCompleted = containsAny(
                text,
                "\ub3c8\uae4c\uc9c0 \uc785\uae08\ud588",
            "\ub3c8\uae4c\uc9c0 \uc785\uae08\ud55c \ub4a4",
                "\ub3c8\uc744 \uc785\uae08\ud588",
                "\ub3c8\uc744 \uc785\uae08\ud55c \ub4a4",
                "\uc2e4\uc81c\ub85c \uc785\uae08\ud588",
                "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uae4c\uc9c0 \uc785\uae08\ud588"
        );

        return loginCompleted && fundingCompleted;
    }

    private boolean containsPostBlockReentryLoginCompleted(
            String text
    ) {
        boolean unblockCompleted = containsAny(
                text,
                "\uacc4\uc815 \ucc28\ub2e8\uc744 \ud574\uc81c\ud55c \ub4a4",
                "\uacc4\uc815 \ucc28\ub2e8\uc744 \ud574\uc81c\ud588\uace0",
                "차단을 다시 해제했고",
                "\uacc4\uc815 \ucc28\ub2e8\uc740 \uc774\ubbf8 \ud574\uc81c\ud588\uace0",
                "\uacc4\uc815 \ucc28\ub2e8\uc744 \ud574\uc81c\ud558\uace0",
                "\uc2e4\uc81c\ub85c \uacc4\uc815 \ucc28\ub2e8\ub3c4 \ud574\uc81c\ud588",
                "\uc2e4\uc81c\ub85c \ucc28\ub2e8 \ud574\uc81c\uae4c\uc9c0 \uc644\ub8cc\ud588"
        );

        boolean loginCompleted = containsAny(
                text,
                "\uc2e4\uc81c\ub85c \ub85c\uadf8\uc778\uae4c\uc9c0 \ud588",
                "\uc2e4\uc81c\ub85c \ub85c\uadf8\uc778\ud588",
                "\uc2e4\uc81c\ub85c \ub85c\uadf8\uc778\ud55c",
                "\ub85c\uadf8\uc778\ud55c \ub4a4",
                "\ub85c\uadf8\uc778\ud588\uace0",
                "\ub85c\uadf8\uc778\ud574\uc11c",
                "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588",
                "\uc2e4\uc81c \ub85c\uadf8\uc778\uae4c\uc9c0 \ud588",
                "\uc2e4\uc81c \ub85c\uadf8\uc778\uae4c\uc9c0 \ud558\uace0"
        );

        return unblockCompleted && loginCompleted;
    }

    private RecoveryRiskPreparationMetadata
            resolveRiskPreparationMetadata(String text) {

        if (containsReentrySelfExitWithRetryIntent(text)) {
            return RecoveryRiskPreparationMetadata.detected(
                    "REENTRY_SELF_EXIT_WITH_RETRY_INTENT",
                    0.90,
                    "the user self-exited before wagering "
                    + "but retained explicit retry intent"
            );
        }

        boolean protectiveBlockReversalPreparation =
                containsProtectiveBlockReversalPreparation(
                        text
                );

        if (protectiveBlockReversalPreparation) {
            return RecoveryRiskPreparationMetadata.detected(
                    "PROTECTIVE_BLOCK_REVERSAL_"
                    + "PREPARATION_PRESENT",
                    0.85,
                    "a completed protective block was followed "
                    + "by concrete account-unblock contact lookup"
            );
        }

        boolean protectiveBlockReversal =
                containsProtectiveBlockReversalPossibility(
                        text
                );

        boolean selfExitBeforeWager =
                containsReentrySelfExitBeforeWager(text);

        if (
                protectiveBlockReversal
                && selfExitBeforeWager
                && isSelfExitLaterThanProtectiveBlock(text)
        ) {
            return RecoveryRiskPreparationMetadata.detected(
                    "REENTRY_COMPLETED_THEN_"
                    + "SELF_EXIT_BEFORE_WAGER",
                    0.85,
                    "site reentry and self-exit occurred after "
                    + "the earlier protective block reversal risk"
            );
        }

        if (protectiveBlockReversal) {
            return RecoveryRiskPreparationMetadata.detected(
                    "PROTECTIVE_BLOCK_REVERSAL_"
                    + "POSSIBILITY_PRESENT",
                    0.85,
                    "a completed protective block may be reversed later"
            );
        }

        if (selfExitBeforeWager) {
            return RecoveryRiskPreparationMetadata.detected(
                    "REENTRY_COMPLETED_THEN_"
                    + "SELF_EXIT_BEFORE_WAGER",
                    0.85,
                    "site reentry was completed but the user "
                    + "self-exited before placing a wager"
            );
        }

        if (
                containsExternalInterventionWithRetryIntent(
                        text
                )
        ) {
            return RecoveryRiskPreparationMetadata.detected(
                    "FUNDING_INTERRUPTED_BY_EXTERNAL_"
                    + "INTERVENTION_WITH_RETRY_INTENT",
                    0.90,
                    "funding was interrupted by external intervention "
                    + "while explicit retry intent remained"
            );
        }

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

    private boolean containsProtectiveBlockReversalPreparation(
            String text
    ) {
        boolean protectiveBlockCompleted =
            containsUnblockRequestSubmittedBeforeFinalConfirmation(text)
            || containsUnblockRequestSubmittedThenCancelled(text)
            || containsUnblockFinalConfirmationSubmitted(text)
            || containsAny(
                text,
                "\uacc4\uc815\uc744 \ub9c9\uc558",
                "\uacc4\uc815\uc744 \ucc28\ub2e8\ud588",
                "\uacc4\uc815\uc744 \ub2e4\uc2dc \ucc28\ub2e8\ud588",
                "\uacc4\uc815\uc744 \uc7a0\uac40",
                "\uacc4\uc815\uc744 \ub9c9\uace0",
                "\uacc4\uc815\uc744 \ucc28\ub2e8\ud558\uace0",
                "\uacc4\uc815\uc744 \ub9c9\uc544\ub193",
                "\uacc4\uc815\uc740 \uc774\ubbf8 \ub9c9\uc544\ub193"
        );

        boolean reversalIntent =
            containsUnblockRequestSubmittedBeforeFinalConfirmation(text)
            || containsUnblockFinalConfirmationSubmitted(text)
            || containsAny(
                text,
                "\ud574\uc81c\ud558\uace0 \uc2f6",
                "\ub2e4\uc2dc \ud480\uace0 \uc2f6",
                "\ub610 \ud480\uace0 \uc2f6\uc740 \uc0dd\uac01",
                "\uacc4\uc815\uc744 \ud480\uace0 \uc2f6\uc740 \uc0dd\uac01",
                "\ub2e4\uc2dc \ud574\uc81c\ud558\uace0 \uc2f6",
                "\ud480 \uc0dd\uac01\uc774 \ub4e4",
                "\ud574\uc81c\ud560 \uc0dd\uac01\uc774 \ub4e4",
            "\ucc28\ub2e8 \ud574\uc81c \ubc29\ubc95",
            "\ud574\uc81c \uc2e0\uccad\uc11c\uae4c\uc9c0 "
                    + "\uc791\uc131\ud588"
        );

        boolean contactLookupCompleted =
            containsUnblockRequestSubmittedBeforeFinalConfirmation(text)
            || containsUnblockRequestSubmittedThenCancelled(text)
            || containsUnblockFinalConfirmationSubmitted(text)
            || containsAny(
                text,
                "\uace0\uac1d\uc13c\ud130 \ubc88\ud638\uae4c\uc9c0 "
                        + "\ucc3e\uc544\ubd24",
                "\uace0\uac1d\uc13c\ud130 \ubc88\ud638\ub97c "
                        + "\ucc3e\uc544\ubd24",
                "\uace0\uac1d\uc13c\ud130 \ubc88\ud638\ub97c \ub2e4\uc2dc "
                        + "\ucc3e\uc544\ubd24",
                "\uace0\uac1d\uc13c\ud130 \ubc88\ud638\uae4c\uc9c0 \ub2e4\uc2dc "
                        + "\ucc3e\uc544\ubd24",
                "\uace0\uac1d\uc13c\ud130 \ubc88\ud638\uae4c\uc9c0 \ub2e4\uc2dc "
                        + "\ucc3e\uc544\ubcf8",
                "\uace0\uac1d\uc13c\ud130 \uc5f0\ub77d\ucc98\ub97c "
                        + "\ucc3e\uc544\ubd24",
                "\ud574\uc81c \ubb38\uc758\ud560 \ubc88\ud638\ub97c "
                        + "\ucc3e\uc544\ubd24",
                "\ud574\uc81c\ud558\ub294 \ubc29\ubc95\uae4c\uc9c0 "
                        + "\uac80\uc0c9\ud574\ubd24",
                "\ud574\uc81c \ubc29\ubc95\uc744 "
                        + "\uac80\uc0c9\ud574\ubd24",
                "\ud574\uc81c \ubc29\ubc95\uc744 \ub2e4\uc2dc "
                        + "\ucc3e\uc544\ubd24",
                "\ud574\uc81c \ubc29\ubc95\uae4c\uc9c0 "
                        + "\uac80\uc0c9\ud574\ubd24",
                "\ucc28\ub2e8 \ud574\uc81c \ubc29\ubc95\uc744 \ub2e4\uc2dc "
                        + "\uac80\uc0c9\ud574\ubd24",
                "\ud574\uc81c \ubb38\uc758 \ud654\uba74\uae4c\uc9c0 "
                        + "\uc5f4\uc5b4\ubd24",
                "\ud574\uc81c \uc2e0\uccad\uc11c\uae4c\uc9c0 "
                        + "\uc791\uc131\ud588",
                "\ud574\uc81c \uc2e0\uccad\uc11c\uae4c\uc9c0 \ub2e4\uc2dc "
                        + "\uc791\uc131\ud588",
                "\uace0\uac1d\uc13c\ud130\uc5d0 \uc804\ud654\ud588",
                "\uace0\uac1d\uc13c\ud130\uc5d0 \uc2e4\uc81c\ub85c \uc804\ud654\uae4c\uc9c0 \ud588",
                "\uc0c1\ub2f4\uc6d0\uacfc \uc5f0\uacb0\uae4c\uc9c0 \ub410",
                "\uacc4\uc815 \ud574\uc81c\ub97c \uc694\uccad\ud588",
            "\ud574\uc81c \uc694\uccad \ubc84\ud2bc\uc744 "
                    + "\ub204\ub974\uae30 \uc9c1\uc804\uae4c\uc9c0 \uac14"
        );

        boolean lookupNegated = containsAny(
                text,
                "\uace0\uac1d\uc13c\ud130 \ubc88\ud638\ub97c "
                        + "\ucc3e\uc544\ubcf4\uc9c0 \uc54a",
                "\uace0\uac1d\uc13c\ud130 \ubc88\ud638\ub294 "
                        + "\ucc3e\uc9c0 \uc54a",
                "\uc5f0\ub77d\ucc98\ub97c \ucc3e\uc9c0 \uc54a",
                "\ud574\uc81c \ubc29\ubc95\uc744 \uac80\uc0c9\ud558\uc9c0 \uc54a",
                "\ud574\uc81c\ud558\ub294 \ubc29\ubc95\uc744 "
                        + "\uac80\uc0c9\ud558\uc9c0 \uc54a"
        );

        boolean protectivePurpose = containsAny(
                text,
                "\ub9c9\uc73c\ub824\uace0 "
                        + "\uace0\uac1d\uc13c\ud130",
                "\ucc28\ub2e8\ud558\ub824\uace0 "
                        + "\uace0\uac1d\uc13c\ud130",
                "\ucc28\ub2e8 \uc694\uccad\uc744 \uc644\ub8cc",
                "\uacc4\uc815\uc744 \ub9c9\uc544\ub2ec\ub77c\uace0 "
                        + "\uc694\uccad",
                "\ub3c4\ubc15\uc744 \ub9c9\ub294 \ubc29\ubc95\uc744 \uac80\uc0c9",
                "\uacc4\uc815\uc744 \ucc28\ub2e8\ud558\ub294 \ubc29\ubc95\uc744 "
                        + "\uac80\uc0c9",
                "\ucc28\ub2e8 \ubc29\ubc95\uc744 \uac80\uc0c9"
        );

        return protectiveBlockCompleted
                && reversalIntent
                && contactLookupCompleted
                && !lookupNegated
                && !protectivePurpose;
    }

    private boolean containsProtectiveBlockReversalPossibility(
            String text
    ) {
        boolean protectiveBlockCompleted =
            containsUnblockRequestSubmittedBeforeFinalConfirmation(text)
            || containsUnblockRequestSubmittedThenCancelled(text)
            || containsUnblockFinalConfirmationSubmitted(text)
            || containsAny(
                text,
                "계정을 막았",
                "계정을 차단했",
                "계정을 잠갔",
                "계정을 막고",
                "계정을 차단하고"
        );

        boolean reversalPossibility = containsAny(
                text,
                "내일 다시 풀 수도",
                "다시 풀 수도",
                "나중에 다시 풀 수도",
                "다시 해제할 수도",
                "내일 다시 해제할 수도"
        );

        boolean reversalNegated = containsAny(
                text,
                "다시 풀 생각은 없",
                "다시는 풀지 않을",
                "다시 해제하지 않을",
                "계속 막아둘"
        );

        return protectiveBlockCompleted
                && reversalPossibility
                && !reversalNegated;
    }

    private boolean containsExternalInterventionWithRetryIntent(
            String text
    ) {
        boolean fundingIntent = containsAny(
                text,
                "계좌에 돈을 옮기려 했",
                "계좌로 돈을 옮기려 했",
                "이체하려 했",
                "송금하려 했",
                "충전하려 했"
        );

        boolean externalIntervention = containsAny(
                text,
                "가족이 휴대폰을 가져가서",
                "가족이 휴대폰을 빼앗아서",
                "휴대폰을 가져가서",
                "휴대폰을 빼앗아서",
                "가족이 막아서"
        );

        boolean fundingNotCompleted = containsAny(
                text,
                "이체하지 못했",
                "송금하지 못했",
                "충전하지 못했",
                "돈을 옮기지 못했"
        );

        boolean retryIntent = containsAny(
                text,
                "내일 다시 시도할 생각",
                "다시 시도할 생각",
                "나중에 다시 시도할 생각",
                "내일 다시 옮길 생각",
                "다시 옮길 생각"
        );

        boolean retryNegated = containsAny(
                text,
                "다시 시도하지 않을",
                "다시는 시도하지 않을",
                "다시 옮기지 않을",
                "이제 그만둘"
        );

        return fundingIntent
                && externalIntervention
                && fundingNotCompleted
                && retryIntent
                && !retryNegated;
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
                "계좌에 돈을 옮기려다가",
                "계좌로 돈을 옮기려다가",
                "이체하려다가",
                "\ucda9\uc804\ud558\ub824\ub2e4\uac00",
                "\ucda9\uc804\uc744 \ud558\ub824\ub2e4\uac00",
                "\uc785\uae08 \ubc84\ud2bc\uae4c\uc9c0 \ub20c\ub800\ub2e4\uac00",
                "\uc785\uae08 \ubc84\ud2bc\uc744 \ub20c\ub800\ub2e4\uac00"
        );

        boolean externallyStopped = containsAny(
                text,
                "오류가 나서",
                "오류 때문에",
                "시간이 없어서",
                "한도가 걸려서",
                "은행 점검 때문에"
        );

        if (externallyStopped) {
            return false;
        }

        boolean fundingCancelled = containsAny(
                text,
                "\ucda9\uc804\uc744 \ucde8\uc18c\ud588",
                "\ucda9\uc804\uc744 \ucde8\uc18c\ud558\uace0",
                "\ucda9\uc804 \uc804\uc5d0 \uba48\ucd84",
                "\uc911\uac04\uc5d0 \uba48\ucd94\uace0",
                "\uc774\uccb4\ub97c \uba48\ucdc4",
                "\uc774\uccb4\ub97c \uc911\ub2e8\ud588",
                "\uc1a1\uae08\uc744 \uba48\ucdc4",
                "\uc1a1\uae08\uc744 \uc911\ub2e8\ud588",
                "\uadf8\ub0e5 \ub098\uc654",
                "\uadf8\ub300\ub85c \ub098\uc654"
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

    private boolean containsSportsBettingEscalationLossRecoveryHelpSeeking(
            String text
    ) {
        boolean sportsBettingPresent = containsAny(
                text,
                "\uc2a4\ud3ec\uce20 \uacbd\uae30"
        );

        boolean actualBettingPresent = containsAny(
                text,
                "\ubca0\ud305\ud588\uc2b5\ub2c8\ub2e4",
                "\ubca0\ud305\ud588"
        );

        boolean amountEscalationPresent = containsAny(
                text,
                "\uae08\uc561\uc774 \ub108\ubb34 \ucee4\uc84c",
                "\ub354 \ud06c\uac8c \uac78\uac8c \ub429"
        );

        boolean lossRecoveryPresent = containsAny(
                text,
                "\uc783\uace0 \ub098\uba74",
                "\ub9cc\ud68c\ud558\ub824\uace0"
        );

        boolean helpSeekingPresent = containsAny(
                text,
                "\ucc3d\ud53c\ud574\uc11c \uc5ec\uae30\uae4c\uc9c0 \uc654",
                "\uc5ec\uae30\uae4c\uc9c0 \uc654\uc2b5\ub2c8\ub2e4"
        );

        return sportsBettingPresent
                && actualBettingPresent
                && amountEscalationPresent
                && lossRecoveryPresent
                && helpSeekingPresent;
    }


    private boolean containsAbstinenceRelapseBettingEscalation(
            String text
    ) {
        boolean abstinencePresent = containsAny(
                text,
                "3\uac1c\uc6d4 \uc815\ub3c4 \ub04a\uc5c8",
                "\ud55c\ub3d9\uc548 3\uac1c\uc6d4"
        );

        boolean actualRelapsePresent = containsAny(
                text,
                "\uacb0\uad6d \ub2e4\uc2dc \ud588",
                "\ub2e4\uc2dc \uc2dc\uc791\ud55c"
        );

        boolean smallRestartIntentPresent = containsAny(
                text,
                "\uc18c\uc561\uc73c\ub85c \ud55c \ubc88\ub9cc \ud574\ubcf4\uc790",
                "\ud55c \ubc88\ub9cc \ud574\ubcf4\uc790"
        );

        boolean escalationPresent = containsAny(
                text,
                "\uae08\uc561\uc774 \uc810\uc810 \ucee4\uc84c",
                "\uc608\uc804\ucc98\ub7fc \uae08\uc561\uc774 \uc810\uc810 \ucee4\uc84c"
        );

        return abstinencePresent
                && actualRelapsePresent
                && smallRestartIntentPresent
                && escalationPresent;
    }


    private boolean containsBigWinMemoryCasinoContinuation(
            String text
    ) {
        boolean slotPresent = containsAny(
                text,
                "\uc2ac\ub86f\uc744 \ud558\ub2e4\uac00",
                "\uc2ac\ub86f"
        );

        boolean bigWinMemoryPresent = containsAny(
                text,
                "\ud070 \uae08\uc561\uc774 \ud55c \ubc88 \ub098\uc628",
                "\uadf8\ub54c\uc758 \uae30\uc5b5"
        );

        boolean repeatWinExpectationPresent = containsAny(
                text,
                "\uc870\uae08\ub9cc \ud558\uba74 \ub2e4\uc2dc \ub098\uc62c \uac83 \uac19\ub2e4",
                "\ub2e4\uc2dc \ub098\uc62c \uac83 \uac19\ub2e4"
        );

        boolean casinoContinuationPresent = containsAny(
                text,
                "\uce74\uc9c0\ub178 \uac8c\uc784\ub3c4 \ubcd1\ud589\ud558\uba74\uc11c",
                "\uce74\uc9c0\ub178 \uac8c\uc784\ub3c4 \ubcd1\ud589"
        );

        return slotPresent
                && bigWinMemoryPresent
                && repeatWinExpectationPresent
                && casinoContinuationPresent;
    }


    private boolean containsDebtDrivenLossRecoveryRepeatedGambling(
            String text
    ) {
        boolean debtPresent = containsAny(
                text,
                "\ube5a\uc774 \uc0dd\uae30\uba74\uc11c",
                "\ube5a\uc774 \uc0dd\uae30"
        );

        boolean gamblingIncreasePresent = containsAny(
                text,
                "\uc624\ud788\ub824 \ub354 \ub3c4\ubc15\uc744 \ud558\uac8c \ub410",
                "\ub354 \ub3c4\ubc15\uc744 \ud558\uac8c \ub410"
        );

        boolean lossRecoveryExpectationPresent = containsAny(
                text,
                "\uc774\ubc88 \ud55c \ubc88\ub9cc \ub530\uba74 \ud574\uacb0\ud560 \uc218 \uc788",
                "\ud55c \ubc88\ub9cc \ub530\uba74 \ud574\uacb0\ud560 \uc218 \uc788"
        );

        boolean repeatedLossPresent = containsAny(
                text,
                "\uacb0\uad6d \ub2e4\uc2dc \ub2e4 \uc783",
                "\ub2e4\uc2dc \ub2e4 \uc783"
        );

        return debtPresent
                && gamblingIncreasePresent
                && lossRecoveryExpectationPresent
                && repeatedLossPresent;
    }


    private boolean containsHabitualBettingSearchAndPersistence(
            String text
    ) {
        boolean repeatedThoughtPresent = containsAny(
                text,
                "\ud558\ub8e8\ub77c\ub3c4 \uc548 \ud558\uba74 \uacc4\uc18d \uc0dd\uac01\ub098",
                "\uacc4\uc18d \uc0dd\uac01\ub098\ub294 \uac8c \ubb38\uc81c"
        );

        boolean bettingPlaceSearchPresent = containsAny(
                text,
                "\ubc30\ud305\ud560 \uacf3\ubd80\ud130 \ucc3e\uace0",
                "\ubc30\ud305\ud560 \uacf3"
        );

        boolean habitualPersistencePresent = containsAny(
                text,
                "\uc0dd\ud65c\uc758 \uc77c\ubd80\uac00 \ub41c",
                "\uc810\uc810 \uc0dd\ud65c\uc758 \uc77c\ubd80"
        );

        return repeatedThoughtPresent
                && bettingPlaceSearchPresent
                && habitualPersistencePresent;
    }


    private boolean containsStressTriggeredCrossGamblingCycleHelpSeeking(
            String text
    ) {
        boolean stressTriggerPresent = containsAny(
                text,
                "\uc57c\uadfc \ud6c4 \uc2a4\ud2b8\ub808\uc2a4",
                "\uc2a4\ud2b8\ub808\uc2a4\ub97c \ud480\ub824\uace0"
        );

        boolean mobileCasinoStarted = containsAny(
                text,
                "\ubaa8\ubc14\uc77c \uce74\uc9c0\ub178\ub97c \uc2dc\uc791\ud588",
                "\ubaa8\ubc14\uc77c \uce74\uc9c0\ub178"
        );

        boolean crossGamblingCyclePresent = containsAny(
                text,
                "\uc2a4\ud3ec\uce20\ubca0\ud305\uc73c\ub85c \ub3cc\ub824\ub193\uc73c\ub824\uace0",
                "\uce74\uc9c0\ub178\ub97c \ucc3e\ub294 \uc2dd\uc73c\ub85c \ubc18\ubcf5"
        );

        boolean persistentThoughtPresent = containsAny(
                text,
                "\ub3c4\ubc15 \uc0dd\uac01\ub9cc \uc790\uafb8 \ub098",
                "\ub3c4\ubc15 \uc0dd\uac01\ub9cc"
        );

        boolean helpSeekingPresent = containsAny(
                text,
                "\uc0c1\ub2f4\uc744 \uc2e0\uccad\ud569\ub2c8\ub2e4",
                "\uc0c1\ub2f4\uc744 \uc2e0\uccad"
        );

        return stressTriggerPresent
                && mobileCasinoStarted
                && crossGamblingCyclePresent
                && persistentThoughtPresent
                && helpSeekingPresent;
    }


    private boolean containsSelfGamblingAfterFriendIntroduction(
            String text
    ) {
        boolean selfContextPresent = containsAny(
                text,
                "\uc800\ub294 \uc6d0\ub798 \ub3c4\ubc15",
                "\uc800\ub294"
        );

        boolean friendIntroductionPresent = containsAny(
                text,
                "\uce5c\uad6c\uac00 \uc54c\ub824\uc918\uc11c",
                "\uce5c\uad6c\uac00 \uc54c\ub824"
        );

        boolean selfGamblingStarted = containsAny(
                text,
                "\uc7ac\ubbf8\uc0bc\uc544 \ud574\ubd24",
                "\ud574\ubd24\ub294\ub370"
        );

        boolean initialBigWinPresent = containsAny(
                text,
                "\ucc98\uc74c\uc5d0 \ud06c\uac8c \ub530\uace0",
                "\ud06c\uac8c \ub530\uace0 \ub098\uc11c"
        );

        boolean lossRecoveryUrgePresent = containsAny(
                text,
                "\ubcf8\uc804 \uc0dd\uac01 \ub54c\ubb38\uc5d0",
                "\ubcf8\uc804 \uc0dd\uac01"
        );

        boolean continuedGamblingPresent = containsAny(
                text,
                "\uacc4\uc18d \ud558\uac8c \ub410",
                "\uacc4\uc18d \ud558\uac8c"
        );

        boolean accumulatedLossPresent = containsAny(
                text,
                "\ud6e8\uc52c \ub9ce\uc774 \uc783\uc5c8",
                "\ub9ce\uc774 \uc783\uc5c8"
        );

        return selfContextPresent
                && friendIntroductionPresent
                && selfGamblingStarted
                && initialBigWinPresent
                && lossRecoveryUrgePresent
                && continuedGamblingPresent
                && accumulatedLossPresent;
    }

}
