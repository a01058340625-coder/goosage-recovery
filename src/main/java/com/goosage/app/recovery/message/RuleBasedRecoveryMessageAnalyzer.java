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

        boolean genericDesireSearchResultWithoutGamblingDomain =
                containsAny(
                        normalized,
                        "다시 하고 싶은 마음이 좀 생겨서",
                        "다시 하고 싶은 마음이 생겨서",
                        "하고 싶은 마음이 좀 생겨서"
                )
                && containsAny(
                        normalized,
                        "검색은 했습니다",
                        "검색은 했",
                        "검색했습니다"
                )
                && containsAny(
                        normalized,
                        "검색어를 끝까지 넣고",
                        "검색어를 끝까지 입력"
                )
                && containsAny(
                        normalized,
                        "결과를 본 뒤",
                        "검색 결과를 본"
                )
                && !containsAny(
                        normalized,
                        "도박",
                        "베팅",
                        "카지노",
                        "슬롯",
                        "배당",
                        "예전에 하던 사이트",
                        "예전에 쓰던 사이트"
                );

        if (genericDesireSearchResultWithoutGamblingDomain) {
            return hold(message, "NO_SUPPORTED_SIGNAL");
        }

        boolean genericPartialSearchDeleteWithoutGamblingDomain =
                containsAny(
                        normalized,
                        "검색창에 사이트 이름을 몇 글자 쳤다가 지웠",
                        "검색창에 사이트 이름을 몇 글자 쳤다가 삭제했"
                )
                && !containsAny(
                        normalized,
                        "도박",
                        "베팅",
                        "카지노",
                        "슬롯",
                        "예전에 하던 사이트",
                        "예전에 쓰던 사이트"
                );

        if (genericPartialSearchDeleteWithoutGamblingDomain) {
            return hold(message, "NO_SUPPORTED_SIGNAL");
        }
        boolean selfContextExtracted = false;
        boolean currentContextExtracted = false;

        boolean thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge = false;
        boolean thirdPartyLinkSelfWagerInputDeleteSelfStop = false;

        boolean thirdPartyGamblingContextForSelfUrge =
                looksLikeThirdPartyContext(normalized)
                && containsAny(
                        normalized,
                        "\ub3c4\ubc15",
                        "\ubca0\ud305",
                        "\uce74\uc9c0\ub178",
                        "\uc2ac\ub86f"
                );

        boolean thirdPartySportsOddsSelfPageAccessAttempt =
                looksLikeThirdPartyContext(normalized)
                && containsAny(
                        normalized,
                        "\ucd95\uad6c \uacbd\uae30",
                        "\uc2a4\ud3ec\uce20 \uacbd\uae30",
                        "\uacbd\uae30"
                )
                && containsAny(
                        normalized,
                        "\ubc30\ub2f9\uc774 \uad1c\ucc2e",
                        "\ubc30\ub2f9",
                        "\ubc30\ub2f9\ub960"
                )
                && containsAny(
                        normalized,
                        "\uad81\uae08\ud574\uc11c \ucc3e\uc544\ubd24",
                        "\ucc3e\uc544\ubd24\uc5b4",
                        "\ucc3e\uc544\ubd24"
                )
                && containsAny(
                        normalized,
                        "\uac80\uc0c9 \uacb0\uacfc",
                        "\uac80\uc0c9\uacb0\uacfc"
                )
                && containsAny(
                        normalized,
                        "\uacbd\uae30 \ud398\uc774\uc9c0\ub3c4 \ub20c\ub7ec",
                        "\uacbd\uae30 \ud398\uc774\uc9c0\ub97c \ub20c\ub7ec",
                        "\uacbd\uae30 \ud398\uc774\uc9c0",
                        "\uc2a4\ud3ec\uce20\ubca0\ud305 \uc0ac\uc774\ud2b8\uac00 \uac80\uc0c9 \uacb0\uacfc",
                        "\uc0ac\uc774\ud2b8\uac00 \uac80\uc0c9 \uacb0\uacfc\uc5d0 \uac19\uc774 \ub5a0",
                        "\ub20c\ub7ec\ubd24\uace0",
                        "\ubc30\ub2f9\ud45c\uae4c\uc9c0 \ud655\uc778"
                );

        boolean thirdPartyHistorySelfSiteSearchAttempt =
                thirdPartyGamblingContextForSelfUrge
                && (
                        containsAny(
                                normalized,
                                "\uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \ubb3c\uc5b4\ubd24",
                                "\uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \ubb3c\uc5b4"
                        )
                        || containsAny(
                                normalized,
                                "지인이 보낸 사이트 이름",
                                "지인이 보내준 사이트 이름"
                        )
                )
                && containsAny(
                        normalized,
                        "\uc81c\uac00 \uac80\uc0c9\ud574\ubcf8",
                        "\uc81c\uac00 \uac80\uc0c9\ud574\ubd24",
                        "사이트 이름을 한번 검색해본",
                        "사이트 이름을 한 번 검색해본"
                );

        boolean thirdPartySlotTriggerSelfAppInstallExecuteSelfStopNextDayDelete =
                thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        normalized,
                        "\uc2ac\ub86f\uc5d0\uc11c \ub3c8\uc744 \ub530",
                        "\uc2ac\ub86f"
                )
                && containsAny(
                        normalized,
                        "\uc9d1\uc5d0 \uc640\uc11c \uad00\ub828 \uc571\uc744 \ucc3e\uc544\ubcf4",
                        "\uad00\ub828 \uc571\uc744 \ucc3e\uc544\ubcf4"
                )
                && containsAny(
                        normalized,
                        "\uc124\uce58\ud588",
                        "\uc124\uce58\ud588\uc5b4\uc694"
                )
                && containsAny(
                        normalized,
                        "\uc2e4\ud589\ud574\ubcf4\ub2c8",
                        "\uc2e4\ud589\ud588"
                )
                && containsAny(
                        normalized,
                        "\ub85c\uadf8\uc778 \ud654\uba74\uc774 \ub098\uc654",
                        "\ub85c\uadf8\uc778 \ud654\uba74"
                )
                && containsAny(
                        normalized,
                        "\uc7ac\ubbf8\uac00 \uc5c6\uc5b4\uc11c \uadf8\ub0e5 \uaed0",
                        "\uadf8\ub0e5 \uaed0"
                )
                && containsAny(
                        normalized,
                        "\ub2e4\uc74c \ub0a0 \uc571\ub3c4 \uc0ad\uc81c",
                        "\ub2e4\uc74c \ub0a0 \uc571\uc744 \uc0ad\uc81c",
                        "\ub2e4\uc74c \ub0a0 \uc571\ub3c4 \uc9c0\uc6e0"
                );

        boolean thirdPartyCasinoTriggerSelfSlotAppSearchAutocompleteAttempt =
                thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        normalized,
                        "친구가 예전에 카지노에서 크게 땄던 얘기를 해서",
                        "카지노에서 크게 땄던 얘기를 해서",
                        "카지노 얘기를 해서"
                )
                && containsAny(
                        normalized,
                        "슬롯 앱 이름을 검색해봤",
                        "슬롯 앱 이름을 검색했"
                )
                && containsAny(
                        normalized,
                        "자동완성에 뜨는 것만 좀 봤",
                        "자동완성에 뜨는 것만 봤",
                        "자동완성만 좀 봤"
                )
                && containsAny(
                        normalized,
                        "설치할 생각까지는 없었",
                        "설치할 생각은 없었"
                );
        boolean thirdPartyTriggerSelfBettingSiteAccessFundingScreenNoAmountInput =
                thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        normalized,
                        "\uce5c\uad6c\uac00 \uc54c\ub824\uc900",
                        "\uce5c\uad6c\uac00 \uc54c\ub824\uc918\uc11c",
                        "\uce5c\uad6c\uac00 \uc54c\ub824"
                )
                && containsAny(
                        normalized,
                        "\ubca0\ud305 \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d",
                        "\ub3c4\ubc15 \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d",
                        "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d"
                )
                && containsAny(
                        normalized,
                        "\uc785\uae08 \ud654\uba74"
                )
                && containsAny(
                        normalized,
                        "\uae08\uc561\uc740 \ub123\uc9c0 \uc54a\uc558",
                        "\uae08\uc561\uc744 \ub123\uc9c0 \uc54a\uc558"
                );

        boolean thirdPartyLinkSelfSiteAccessPreLoginSelfStop =
                thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        normalized,
                        "\uce74\uc9c0\ub178 \ub9c1\ud06c\ub97c \ubcf4\ub0b4\uc92c",
                        "\uce74\uc9c0\ub178 \ub9c1\ud06c\ub97c \ubcf4\ub0b4",
                        "\ub9c1\ud06c\ub97c \ubcf4\ub0b4\uc92c\ub294\ub370"
                )
                && containsAny(
                        normalized,
                        "\uacb0\uad6d \ub20c\ub7ec\uc11c",
                        "\ub20c\ub7ec\uc11c"
                )
                && containsAny(
                        normalized,
                        "\uc0ac\uc774\ud2b8 \ud654\uba74\uae4c\uc9c0\ub294 \ubd24",
                        "\uc0ac\uc774\ud2b8 \ud654\uba74\uae4c\uc9c0 \ubd24"
                )
                && containsAny(
                        normalized,
                        "\ub85c\uadf8\uc778\ud558\uae30 \uc804\uc5d0 \ub2eb",
                        "\ub85c\uadf8\uc778 \uc804\uc5d0 \ub2eb"
                );
                boolean genericThirdPartyAppInstallWithoutGamblingDomain =
                containsAny(
                        normalized,
                        "친구가 재미로 해보라고 앱을 보여줬",
                        "친구가 앱을 보여줬"
                )
                && containsAny(
                        normalized,
                        "설치 버튼까지 갔다가",
                        "설치 버튼까지 갔"
                )
                && !containsAny(
                        normalized,
                        "도박",
                        "베팅",
                        "카지노",
                        "슬롯",
                        "배당"
                );

        if (genericThirdPartyAppInstallWithoutGamblingDomain) {
            return hold(message, "NO_SUPPORTED_SIGNAL");
        }
        boolean selfSportsBettingActionBeforeLaterThirdPartyInterruption =
                (
                        containsAny(
                                normalized,
                                "\uc2a4\ud3ec\uce20\ubca0\ud305 \uc0ac\uc774\ud2b8",
                                "\uc2a4\ud3ec\uce20 \ubca0\ud305 \uc0ac\uc774\ud2b8"
                        )
                        && containsAny(
                                normalized,
                                "\uc8fc\uc18c\ub97c \uc9c1\uc811 \uc785\ub825",
                                "\uc0ac\uc774\ud2b8 \uc8fc\uc18c\ub97c \uc9c1\uc811 \uc785\ub825"
                        )
                        && containsAny(
                                normalized,
                                "\ub85c\uadf8\uc778\ub3c4 \uc815\uc0c1\uc801\uc73c\ub85c \uc644\ub8cc",
                                "\ub85c\uadf8\uc778\uc774 \uc815\uc0c1\uc801\uc73c\ub85c \uc644\ub8cc"
                        )
                        && containsAny(
                                normalized,
                                "\uae08\uc561\uc744 \uc785\ub825",
                                "\ubc30\ub2f9\uc744 \uc120\ud0dd\ud558\uace0 \uae08\uc561\uc744 \uc785\ub825"
                        )
                        && containsAny(
                                normalized,
                                "\ub3d9\uc0dd\uc774 \ubc29\uc5d0 \ub4e4\uc5b4\uc640",
                                "\ub3d9\uc0dd\uc774"
                        )
                )
                || (
                        containsAny(
                                normalized,
                                "\uc2a4\ud3ec\uce20\ubca0\ud305\uc744 \ud588",
                                "\uc2a4\ud3ec\uce20 \ubca0\ud305\uc744 \ud588",
                                "\ubca0\ud305\uc744 \ud588"
                        )
                        && containsAny(
                                normalized,
                                "\ub450 \ubc88\uc9f8 \ubca0\ud305\uae4c\uc9c0 \ub05d\ub0b4",
                                "\ub450 \ubc88\uc9f8 \ubca0\ud305\uae4c\uc9c0 \ub05d",
                                "\ub450\ubc88\uc9f8 \ubca0\ud305\uae4c\uc9c0 \ub05d"
                        )
                        && containsAny(
                                normalized,
                                "\ubc30\uc6b0\uc790\uac00 \uc606\uc5d0 \uc640",
                                "\ubc30\uc6b0\uc790\uac00",
                                "\uc544\ub0b4\uac00",
                                "\ub0a8\ud3b8\uc774"
                        )
                );

        boolean selfCasinoSlotActionBeforeLateThirdPartyConversation =
                containsAny(
                        normalized,
                        "\uce74\uc9c0\ub178\uc5d0\uc11c \uc2ac\ub86f\uc744",
                        "\uc2ac\ub86f\uc744 \uba87 \ucc28\ub840 \ub3cc\ub838",
                        "\uc2ac\ub86f\uc744 \ub3cc\ub838"
                )
                && containsAny(
                        normalized,
                        "\ub2e4\uc2dc \uc785\uae08\uc744 \ud558\uace0",
                        "\uc785\uae08\uc744 \ud558\uace0 \uacc4\uc18d",
                        "\uacc4\uc18d \ub3cc\ub838"
                )
                && containsAny(
                        normalized,
                        "\uce5c\uad6c\uac00 \uc65c \uc694\uc998 \uce74\uc9c0\ub178",
                        "\uce5c\uad6c\uac00",
                        "\uce74\uc9c0\ub178 \uc598\uae30\ub97c \uc548 \ud558"
                );

        if (
                looksLikeThirdPartyContext(normalized)
                && !containsSelfGamblingAfterFriendIntroduction(normalized)
                && !selfSportsBettingActionBeforeLaterThirdPartyInterruption
                && !selfCasinoSlotActionBeforeLateThirdPartyConversation
        ) {
            int selfSubjectIndex =
                    findExplicitSelfSubjectAfterThirdParty(normalized);

            int implicitSelfSearchThoughtIndex = firstIndexOfAny(
                    normalized,
                    "\uc61b\ub0a0 \uc2b5\uad00\uc774 \uc790\uafb8 \ub5a0\uc624\ub978",
                    "\uc61b\ub0a0 \uc2b5\uad00\uc774 \ub5a0\uc624\ub978"
            );

            if (
                    implicitSelfSearchThoughtIndex >= 0
                    && containsAny(
                            normalized,
                            "\uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544\ubcfc\uae4c"
                    )
                    && containsAny(
                            normalized,
                            "\uac80\uc0c9\uc740 \ud558\uc9c0 \uc54a\uc558"
                    )
                    && (
                            selfSubjectIndex < 0
                            || implicitSelfSearchThoughtIndex < selfSubjectIndex
                    )
            ) {
                selfSubjectIndex = implicitSelfSearchThoughtIndex;
            }

            int implicitSelfSportsSiteSearchIndex = firstIndexOfAny(
                    normalized,
                    "\uc5b4\ub5a4 \uc0ac\uc774\ud2b8\uc778\uc9c0 \ucc3e\uc544\ubd24",
                    "\uc0ac\uc774\ud2b8\uc778\uc9c0 \ucc3e\uc544\ubd24"
            );

            if (
                    implicitSelfSportsSiteSearchIndex >= 0
                    && thirdPartyGamblingContextForSelfUrge
                    && containsAny(
                            normalized,
                            "\uc2a4\ud3ec\uce20\ubca0\ud305"
                    )
                    && containsAny(
                            normalized,
                            "\uac80\uc0c9 \uacb0\uacfc\uc5d0 \ub098\uc628 \uc774\ub984",
                            "\ubc30\ub2f9 \ud654\uba74"
                    )
                    && (
                            selfSubjectIndex < 0
                            || implicitSelfSportsSiteSearchIndex < selfSubjectIndex
                    )
            ) {
                selfSubjectIndex = implicitSelfSportsSiteSearchIndex;
            }
            if (selfSubjectIndex < 0) {

                boolean implicitSelfAfterThirdPartyTrigger =
                        (
                                thirdPartyGamblingContextForSelfUrge
                                && (
                                        (
                                                containsAny(
                                                        normalized,
                                                        "\uc608\uc804\uc5d0 \uc783\uc740 \ub3c8 \uc0dd\uac01",
                                                        "\uc783\uc740 \ub3c8 \uc0dd\uac01"
                                                )
                                                && containsAny(
                                                        normalized,
                                                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud588",
                        "\uc608\uc804\uc5d0 \ubcf4\ub358 \uacbd\uae30 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9",
                        "\uacbd\uae30 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9",
                                                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                                                )
                                        )
                                        || (
                                                containsAny(
                                                        normalized,
                                                        "\uc7a0\uae50 \ud754\ub4e4\ub838",
                                                        "\ud754\ub4e4\ub838"
                                                )
                                                && containsAny(
                                                        normalized,
                                                        "\uc571\uc744 \ucc3e\uc544\ubcf4\uae30",
                                                        "\uc571\uc744 \ucc3e\uc544\ubcf4",
                                                        "\uc571\uc744 \ucc3e"
                                                )
                                        )
                                )
                        )
                        || (
                                containsAny(
                                        normalized,
                                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uae4c\uc9c0 \ub5a0\uc62c\ub790",
                                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub5a0\uc62c\ub790"
                                )
                                && containsIndirectGamblingSiteSearchAttempt(normalized)
                                && containsAny(
                                        normalized,
                                        "\uc2e4\uc81c\ub85c \ub3c8\uc744 \ub123\uc740 \uac74 \uc544\ub2c8",
                                        "\uc2e4\uc81c\ub85c \ub3c8\uc744 \ub123\uc740 \uac83\uc740 \uc544\ub2c8"
                                )
                        )
                        || (
                                containsAny(
                                        normalized,
                                        "\uacc4\uc18d \uadf8 \ub9d0\uc774 \ub5a0\uc624\ub974",
                                        "\uadf8 \ub9d0\uc774 \ub5a0\uc624\ub974"
                                )
                                && containsAny(
                                        normalized,
                                        "\uad00\ub828 \uc571\uc744 \ucc3e\uc544\ubcfc\uae4c",
                                        "\uc571\uc744 \ucc3e\uc544\ubcfc\uae4c"
                                )
                        )
                        || (
                                containsAny(
                                        normalized,
                                        "\uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544\ubcf4\ub824\ub2e4\uac00 \ub9d0\uc558"
                                )
                                && containsAny(
                                        normalized,
                                        "\uac80\uc0c9\ub3c4 \uc548 \ud588"
                                )
                        )
                        || (

                                thirdPartyGamblingContextForSelfUrge

                                && containsAny(

                                        normalized,

                                        "\uc608\uc804\uc5d0 \uc4f0\ub358 \uc0ac\uc774\ud2b8\uac00 \ub5a0\uc62c\ub790"

                                )

                                && containsAny(

                                        normalized,

                                        "\uac80\uc0c9\ud574\uc11c \uc774\ub984\ub9cc \ud655\uc778"

                                )

                        )
                        || (

                                thirdPartyGamblingContextForSelfUrge

                                && containsAny(

                                        normalized,

                                        "\uc800\ub3c4 \uc571\uc774 \uc788\ub294\uc9c0 \ucc3e\uc544\ubd24"

                                )

                                && containsAny(

                                        normalized,

                                        "\uac80\uc0c9 \uacb0\uacfc \uba87 \uac1c \ub098\uc628 \uac83\ub9cc \ubcf4\uace0",
                                        "\uc5b4\ub5a4 \uac74\uc9c0\ub294 \ud655\uc778\ud588"

                                )

                        )
                        || (
                                thirdPartyGamblingContextForSelfUrge
                                && containsAny(
                                        normalized,
                                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \uac11\uc790\uae30 \uc0dd\uac01\ub098"
                                )
                                && containsAny(
                                        normalized,
                                        "\uac80\uc0c9\ucc3d\uc744 \uc5f4"
                                )
                                && containsAny(
                                        normalized,
                                        "\uba87 \uae00\uc790 \uce58\ub2e4\uac00",
                                        "\uba87 \uae00\uc790 \uce58"
                                )
                                && containsAny(
                                        normalized,
                                        "\uadf8\ub0e5 \ub2eb\uc558",
                                        "\ub2eb\uc558"
                                )
                        || (
                                thirdPartyGamblingContextForSelfUrge
                                && containsAny(
                                        normalized,
                                        "관련 사이트를 한 번 찾아봤",
                                        "관련 사이트를 찾아봤",
                                        "관련 사이트를 검색했"
                                )
                                && containsAny(
                                        normalized,
                                        "로그인 화면까지는 들어갔",
                                        "로그인 화면까지 들어갔",
                                        "로그인 화면까지 들어가"
                                )
                        )
                        );


                boolean lateDomainThirdPartyLinkSelfCasinoAccess =
                        containsAny(
                                normalized,
                                "\ub9c1\ud06c \ud558\ub098\ub97c \ubcf4\ub0b4\uc918\uc11c",
                                "\ub9c1\ud06c\ub97c \ud558\ub098 \ubcf4\ub0b4\uc918\uc11c",
                                "\ub9c1\ud06c\ub97c \ubcf4\ub0b4\uc918\uc11c"
                        )
                        && containsAny(
                                normalized,
                                "\ub20c\ub7ec\ubd24",
                                "\ub20c\ub800"
                        )
                        && containsAny(
                                normalized,
                                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8\uc778 \uac74 \ub4e4\uc5b4\uac00\uace0 \ub098\uc11c \uc54c\uc558",
                                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8\uc778 \uac83\uc740 \ub4e4\uc5b4\uac00\uace0 \ub098\uc11c \uc54c\uc558",
                                "\ub4e4\uc5b4\uac00\uace0 \ub098\uc11c \uce74\uc9c0\ub178"
                        );

                thirdPartyLinkSelfWagerInputDeleteSelfStop =
                containsAny(
                        normalized,
                        "\uce5c\uad6c\uac00 \ubcf4\ub0b4\uc900 \ub9c1\ud06c",
                        "\uce5c\uad6c\uac00 \ubcf4\ub0b8 \ub9c1\ud06c",
                        "\ub3d9\ub8cc\uac00 \ubcf4\ub0b4\uc900 \ub9c1\ud06c",
                        "\ub3d9\ub8cc\uac00 \ubcf4\ub0b8 \ub9c1\ud06c"
                )
                && containsAny(
                        normalized,
                        "\uc2a4\ud3ec\uce20 \uacbd\uae30 \ubc30\ub2f9",
                        "\uacbd\uae30 \ubc30\ub2f9",
                        "\ubc30\ub2f9\uc774 \ub098\uc624\ub294 \ud398\uc774\uc9c0"
                )
                && containsAny(
                        normalized,
                        "\uacbd\uae30 \ud558\ub098\ub97c \uc120\ud0dd",
                        "\uc624\ub298 \uacbd\uae30 \ud558\ub098\ub97c \uc120\ud0dd",
                        "\uacbd\uae30\ub97c \uc120\ud0dd"
                )
                && containsAny(
                        normalized,
                        "\ubca0\ud305 \uae08\uc561\uc744 \uc785\ub825",
                        "\ubca0\ud305\uae08\uc561\uc744 \uc785\ub825",
                        "\uae08\uc561\uc744 \uc785\ub825"
                )
                && containsAny(
                        normalized,
                        "\uc81c\ucd9c \ubc84\ud2bc\uc744 \ub204\ub974\uae30 \uc9c1\uc804",
                        "\uc81c\ucd9c \uc9c1\uc804"
                )
                && containsAny(
                        normalized,
                        "\uae08\uc561\uc744 \uc9c0\uc6b0",
                        "\uae08\uc561\uc744 \uc9c0\uc6e0"
                )
                && containsAny(
                        normalized,
                        "\ud398\uc774\uc9c0\ub97c \ub2eb",
                        "\ud398\uc774\uc9c0\ub97c \ub2eb\uc558"
                );

        boolean thirdPartyLinkSelfCasinoAccess =
                        thirdPartyGamblingContextForSelfUrge
                        && containsAny(
                                normalized,
                                "\ub9c1\ud06c\ub97c \ud558\ub098 \ubcf4\ub0b4\uc918\uc11c",
                                "\ub9c1\ud06c\ub97c \ubcf4\ub0b4\uc918\uc11c"
                        )
                        && containsAny(
                                normalized,
                                "\ub20c\ub800\ub294\ub370",
                                "\ub20c\ub800"
                        )
                        && containsAny(
                                normalized,
                                "\uce74\uc9c0\ub178 \ud654\uba74\uc774 \ub098\uc624",
                                "\uce74\uc9c0\ub178 \ud654\uba74"
                        );

                boolean thirdPartyCasinoTriggerSelfAdLoginIdInput =
                        thirdPartyGamblingContextForSelfUrge
                        && containsAny(
                                normalized,
                                "\uc9d1\uc5d0 \uc640\uc11c \uac80\uc0c9\ud574\ubd24",
                                "\uc9d1\uc5d0 \uc640\uc11c \uac80\uc0c9"
                        )
                        && containsAny(
                                normalized,
                                "\uc0ac\uc774\ud2b8 \uad11\uace0\uac00 \uac19\uc774 \ub5a0\uc11c \ub20c\ub800",
                                "\uad11\uace0\uac00 \uac19\uc774 \ub5a0\uc11c \ub20c\ub800",
                                "\uad11\uace0\ub97c \ub20c\ub800"
                        )
                        && containsAny(
                                normalized,
                                "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \uac14",
                                "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0"
                        )
                        && containsAny(
                                normalized,
                                "\uc608\uc804 \uc544\uc774\ub514\ub97c \uc785\ub825",
                                "\uc544\uc774\ub514\ub97c \uc785\ub825"
                        );

                boolean thirdPartyBettingTriggerSelfSearchConsiderationUrge =
                        thirdPartyGamblingContextForSelfUrge
                        && containsAny(
                                normalized,
                                "\uc608\uc804\uc5d0 \ub3c4\ubc15\uc744 \uc880 \ud588",
                                "\uc608\uc804\uc5d0 \ub3c4\ubc15"
                        )
                        && containsAny(
                                normalized,
                                "\uacbd\uae30 \ubca0\ud305 \uc774\uc57c\uae30",
                                "\ubca0\ud305 \uc774\uc57c\uae30"
                        )
                        && containsAny(
                                normalized,
                                "\uac11\uc790\uae30 \uadf8\ub54c\uac00 \uc0dd\uac01",
                                "\uadf8\ub54c\uac00 \uc0dd\uac01"
                        )
                        && containsAny(
                                normalized,
                                "\uac80\uc0c9\uae4c\uc9c0 \ud560\uae4c \ub9d0\uae4c",
                                "\uac80\uc0c9\ud560\uae4c \ub9d0\uae4c"
                        )
                        && containsAny(
                                normalized,
                                "\uc2e4\uc81c\ub85c \uac80\uc0c9\ud558\uac70\ub098 \uc0ac\uc774\ud2b8\uc5d0 \ub4e4\uc5b4\uac00\uc9c0\ub294 \uc54a",
                                "\uc2e4\uc81c\ub85c \uac80\uc0c9\ud558\uc9c0\ub294 \uc54a"
                        );

                thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge =
                        thirdPartyGamblingContextForSelfUrge
                        && containsAny(
                                normalized,
                                "\uce5c\uad6c\uac00 \ub2e8\uccb4\ubc29\uc5d0 \uacbd\uae30 \uacb0\uacfc\ub97c \uc62c\ub9b0",
                                "\uce5c\uad6c\uac00 \ub2e8\uccb4\ubc29",
                                "\uacbd\uae30 \uacb0\uacfc\ub97c \uc62c\ub9b0"
                        )
                        && containsAny(
                                normalized,
                                "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud574\uc11c \uc811\uc18d",
                                "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud574 \uc811\uc18d",
                                "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                        )
                        && containsAny(
                                normalized,
                                "\ub85c\uadf8\uc778\uae4c\uc9c0",
                                "\ub85c\uadf8\uc778"
                        )
                        && containsAny(
                                normalized,
                                "\uc2e4\uc81c\ub85c \ub3c8\uc744 \ub123\uace0",
                                "\ub3c8\uc744 \ub123\uace0"
                        )
                        && containsAny(
                                normalized,
                                "\uc2ac\ub86f\uc744 \uc2dc\uc791",
                                "\uc2ac\ub86f\uc744 \uc2dc\uc791\ud588"
                        )
                        && containsAny(
                                normalized,
                                "\ub354 \uc624\ub798 \ud558\uac8c",
                                "\ucc98\uc74c \uba87 \ubc88\uc740 \ub530",
                                "\ucc98\uc74c \uba87\ubc88\uc740 \ub530"
                        )
                        && containsAny(
                                normalized,
                                "\uc2a4\uc2a4\ub85c \uc885\ub8cc",
                                "\uc2a4\uc2a4\ub85c \uc885\ub8cc\ud588"
                        )
                        && containsAny(
                                normalized,
                                "\uacc4\uc18d \uc0dd\uac01\uc740 \ub0a9",
                                "\uacc4\uc18d \uc0dd\uac01",
                                "\uc624\ub298\uc740 \ub2e4\uc2dc \uc811\uc18d\ud558\uc9c0 \uc54a\uc558"
                        );

                boolean thirdPartyBettingTriggerSelfGameSiteSearch =
                        thirdPartyGamblingContextForSelfUrge
                        && containsAny(
                                normalized,
                                "\uc608\uc804\uc5d0 \ubcf4\ub358 \uacbd\uae30 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9",
                                "\uacbd\uae30 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                        );

                implicitSelfAfterThirdPartyTrigger =
                        implicitSelfAfterThirdPartyTrigger
                        || thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge
                        || thirdPartyBettingTriggerSelfGameSiteSearch
                        || thirdPartyBettingTriggerSelfSearchConsiderationUrge
                        || thirdPartyCasinoTriggerSelfAdLoginIdInput
                        || thirdPartySlotTriggerSelfAppInstallExecuteSelfStopNextDayDelete
                        || thirdPartyCasinoTriggerSelfSlotAppSearchAutocompleteAttempt
                        || thirdPartyHistorySelfSiteSearchAttempt
                        || thirdPartyLinkSelfWagerInputDeleteSelfStop
                        || thirdPartyLinkSelfCasinoAccess
                        || lateDomainThirdPartyLinkSelfCasinoAccess
                        || thirdPartyLinkSelfSiteAccessPreLoginSelfStop
                        || thirdPartyTriggerSelfBettingSiteAccessFundingScreenNoAmountInput
                        || thirdPartySportsOddsSelfPageAccessAttempt;

                if (!implicitSelfAfterThirdPartyTrigger) {
                    return hold(message, "THIRD_PARTY_CONTEXT");
                }

                selfSubjectIndex = firstIndexOfAny(
                        normalized,
                        "슬롯 앱 이름을 검색해봤",
                        "슬롯 앱 이름을 검색했",
                        "\uc81c\uac00 \uac80\uc0c9\ud574\ubcf8",
                        "\uc81c\uac00 \uac80\uc0c9\ud574\ubd24",
                        "사이트 이름을 한번 검색해본",
                        "사이트 이름을 한 번 검색해본",
                        "\uc608\uc804\uc5d0 \uc783\uc740 \ub3c8 \uc0dd\uac01",
                        "\uc783\uc740 \ub3c8 \uc0dd\uac01",
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud588",
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uae4c\uc9c0 \ub5a0\uc62c\ub790",
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub5a0\uc62c\ub790",
                        "\uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544\ubcf4\ub824\ub2e4\uac00 \ub9d0\uc558",
                        "\uc608\uc804\uc5d0 \uc4f0\ub358 \uc0ac\uc774\ud2b8\uac00 \ub5a0\uc62c\ub790",
                        "\uc800\ub3c4 \uc571\uc774 \uc788\ub294\uc9c0 \ucc3e\uc544\ubd24",
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \uac11\uc790\uae30 \uc0dd\uac01\ub098",
                        "\uc7a0\uae50 \ud754\ub4e4\ub838",
                        "\ud754\ub4e4\ub838",
                        "\uc571\uc744 \ucc3e\uc544\ubcf4\uae30",
                        "\uad00\ub828 \uc571\uc744 \ucc3e\uc544\ubcfc\uae4c",
                        "\uc571\uc744 \ucc3e\uc544\ubcfc\uae4c",
                        "\ub20c\ub800\ub294\ub370",
                        "관련 사이트를 한 번 찾아봤",
                        "관련 사이트를 찾아봤",
                        "관련 사이트를 검색했"
                );

                if (
                        selfSubjectIndex < 0
                        && thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud574\uc11c \uc811\uc18d",
                            "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                    );
                }

                if (
                        selfSubjectIndex < 0
                        && thirdPartyBettingTriggerSelfSearchConsiderationUrge
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\uac11\uc790\uae30 \uadf8\ub54c\uac00 \uc0dd\uac01",
                            "\uadf8\ub54c\uac00 \uc0dd\uac01"
                    );
                }

                if (
                        selfSubjectIndex < 0
                        && thirdPartySlotTriggerSelfAppInstallExecuteSelfStopNextDayDelete
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\uc9d1\uc5d0 \uc640\uc11c \uad00\ub828 \uc571\uc744 \ucc3e\uc544\ubcf4",
                            "\uad00\ub828 \uc571\uc744 \ucc3e\uc544\ubcf4"
                    );
                }

                if (
                        selfSubjectIndex < 0
                        && thirdPartyCasinoTriggerSelfAdLoginIdInput
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\uc9d1\uc5d0 \uc640\uc11c \uac80\uc0c9\ud574\ubd24",
                            "\uc9d1\uc5d0 \uc640\uc11c \uac80\uc0c9"
                    );
                }

                if (
                        selfSubjectIndex < 0
                        && lateDomainThirdPartyLinkSelfCasinoAccess
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\ub20c\ub7ec\ubd24",
                            "\ub20c\ub800"
                    );
                }

                if (
                        selfSubjectIndex < 0
                        && thirdPartyLinkSelfWagerInputDeleteSelfStop
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\uacbd\uae30 \ud558\ub098\ub97c \uc120\ud0dd",
                            "\uc624\ub298 \uacbd\uae30 \ud558\ub098\ub97c \uc120\ud0dd",
                            "\ubca0\ud305 \uae08\uc561\uc744 \uc785\ub825",
                            "\uae08\uc561\uc744 \uc785\ub825"
                    );
                }

                if (
                        selfSubjectIndex < 0
                        && thirdPartyLinkSelfSiteAccessPreLoginSelfStop
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\uacb0\uad6d \ub20c\ub7ec\uc11c",
                            "\ub20c\ub7ec\uc11c"
                    );
                }
                if (
                        selfSubjectIndex < 0
                        && thirdPartySportsOddsSelfPageAccessAttempt
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\uad81\uae08\ud574\uc11c \ucc3e\uc544\ubd24",
                            "\ucc3e\uc544\ubd24\uc5b4",
                            "\ucc3e\uc544\ubd24"
                    );
                }

                if (
                        selfSubjectIndex < 0
                        && thirdPartyTriggerSelfBettingSiteAccessFundingScreenNoAmountInput
                ) {
                    selfSubjectIndex = firstIndexOfAny(
                            normalized,
                            "\ubca0\ud305 \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d",
                            "\ub3c4\ubc15 \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d",
                            "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d"
                    );
                }

                if (selfSubjectIndex < 0) {
                    return hold(message, "THIRD_PARTY_CONTEXT");
                }
            }

            analysisText = normalized.substring(selfSubjectIndex);
            selfContextExtracted = true;
        }

        boolean priorGamblingContextForCurrentThought =
                containsAny(
                        analysisText,
                        "\ub3c4\ubc15",
                        "\ubca0\ud305",
                        "\uce74\uc9c0\ub178",
                        "\uc2ac\ub86f"
                )
                || (
                        containsAny(
                                analysisText,
                                "\uc2a4\ud3ec\uce20 \uacbd\uae30",
                                "\uacbd\uae30\uc5d0 \ub3c8\uc744"
                        )
                        && containsAny(
                                analysisText,
                                "\ub3c8\uc744 \uc790\uc8fc \uac78",
                                "\ub3c8\uc744 \uac78\uc5c8"
                        )
                );

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

        boolean selfLossThoughtAfterThirdPartyTrigger =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\uc608\uc804\uc5d0 \uc783\uc740 \ub3c8 \uc0dd\uac01",
                        "\uc783\uc740 \ub3c8 \uc0dd\uac01"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud588",
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                );

        boolean selfUrgeSearchAfterThirdPartyTrigger =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\uc7a0\uae50 \ud754\ub4e4\ub838",
                        "\ud754\ub4e4\ub838"
                )
                && containsAny(
                        analysisText,
                        "\uc571\uc744 \ucc3e\uc544\ubcf4\uae30",
                        "\uc571\uc744 \ucc3e\uc544\ubcf4",
                        "\uc571\uc744 \ucc3e"
                );

        boolean selfUrgeAfterThirdPartyTrigger =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\ub098\ub3c4 \ud574\ubcfc\uae4c",
                        "\ud574\ubcfc\uae4c \ud558\ub294 \uc0dd\uac01",
                        "\ud574\ubcfc\uae4c \ud558\ub294 \uc0dd\uac01\uc740",
                        "\uac80\uc0c9\uae4c\uc9c0 \ud560\uae4c \ub9d0\uae4c",
                        "\uac80\uc0c9\ud560\uae4c \ub9d0\uae4c"
                );

        boolean selfSearchInputAfterThirdPartyTrigger =
                selfContextExtracted
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uae4c\uc9c0 \ub5a0\uc62c\ub790",
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub5a0\uc62c\ub790"
                )
                && containsIndirectGamblingSiteSearchAttempt(analysisText)
                && containsAny(
                        analysisText,
                        "\uc2e4\uc81c\ub85c \ub3c8\uc744 \ub123\uc740 \uac74 \uc544\ub2c8",
                        "\uc2e4\uc81c\ub85c \ub3c8\uc744 \ub123\uc740 \uac83\uc740 \uc544\ub2c8"
                );

        boolean thirdPartyHistoryExplicitSelfSiteSearch =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8\uac00 \ubb54\uc9c0\ub9cc \uac80\uc0c9\ud574\ubd24",
                        "\uadf8 \uc0ac\uc774\ud2b8\uac00 \ubb54\uc9c0\ub9cc \uac80\uc0c9\ud574\ubd24",
                        "\uc0ac\uc774\ud2b8\uac00 \ubb54\uc9c0 \uac80\uc0c9\ud574\ubd24"
                )
                && containsAny(
                        analysisText,
                        "\uc2e4\uc81c\ub85c \uc774\uc6a9\ud55c \uc801\uc740 \uc5c6",
                        "\uc2e4\uc81c \uc774\uc6a9\ud55c \uc801\uc740 \uc5c6"
                );
        boolean selfSiteSearchAfterThirdPartyTrigger =

                selfContextExtracted

                && thirdPartyGamblingContextForSelfUrge

                && containsAny(

                        analysisText,

                        "\uc608\uc804\uc5d0 \uc4f0\ub358 \uc0ac\uc774\ud2b8\uac00 \ub5a0\uc62c\ub790"

                )

                && containsAny(

                        analysisText,

                        "\uac80\uc0c9\ud574\uc11c \uc774\ub984\ub9cc \ud655\uc778"

                );

        boolean selfAppSearchAfterThirdPartyTrigger =

                selfContextExtracted

                && thirdPartyGamblingContextForSelfUrge

                && containsAny(

                        analysisText,

                        "\uc800\ub3c4 \uc571\uc774 \uc788\ub294\uc9c0 \ucc3e\uc544\ubd24"

                )

                && containsAny(

                        analysisText,

                        "\uac80\uc0c9 \uacb0\uacfc \uba87 \uac1c \ub098\uc628 \uac83\ub9cc \ubcf4\uace0",
                        "\uc5b4\ub5a4 \uac74\uc9c0\ub294 \ud655\uc778\ud588"

                )

                && containsAny(

                        analysisText,

                        "\uc2e4\ud589\uae4c\uc9c0 \ud558\uc9c0\ub294 \uc54a\uc558"

                );

        boolean searchThoughtWithoutAttempt =
                (
                        containsAny(
                                analysisText,
                                "\uc61b\ub0a0 \uc2b5\uad00\uc774 \uc790\uafb8 \ub5a0\uc624\ub978",
                                "\uc61b\ub0a0 \uc2b5\uad00\uc774 \ub5a0\uc624\ub978"
                        )
                        && containsAny(
                                analysisText,
                                "\uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544\ubcfc\uae4c"
                        )
                        && containsAny(
                                analysisText,
                                "\uac80\uc0c9\uc740 \ud558\uc9c0 \uc54a\uc558"
                        )
                )
                || (
                        containsAny(
                                analysisText,
                                "\ub2e4\uc2dc \ube44\uc2b7\ud55c \uae30\ubd84\uc774 \uc62c\ub77c\uc654\ub2e4",
                                "\ube44\uc2b7\ud55c \uae30\ubd84\uc774 \uc62c\ub77c\uc654\ub2e4"
                        )
                        && containsAny(
                                analysisText,
                                "\uad00\ub828 \uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub5a0\uc62c\ub790\ub2e4",
                                "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub5a0\uc62c\ub790\ub2e4"
                        )
                        && containsAny(
                                analysisText,
                                "\uac80\uc0c9\ucc3d\uc744 \uc5f4\uc5c8\ub2e4\uac00"
                        )
                        && containsAny(
                                analysisText,
                                "\uc544\ubb34\uac83\ub3c4 \uc785\ub825\ud558\uc9c0 \uc54a\uace0",
                                "\uc785\ub825\ud558\uc9c0 \uc54a\uace0"
                        )
                )
                || (
                        containsAny(
                                analysisText,
                                "\uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544\ubcf4\ub824\ub2e4\uac00 \ub9d0\uc558"
                        )
                        && containsAny(
                                analysisText,
                                "\uac80\uc0c9\ub3c4 \uc548 \ud588"
                        )
                );

        boolean partialSearchInputThenSelfStopped =
                containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub5a0\uc62c\ub790",
                        "\uce74\uc9c0\ub178 \uc0dd\uac01\uc774 \uc790\uafb8 \ub0a9\ub2c8\ub2e4",
                        "\uce74\uc9c0\ub178 \uc0dd\uac01\uc774 \uc790\uafb8 \ub098"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9\ucc3d\uc5d0 \ub4e4\uc5b4\uac00\uc11c",
                        "\uac80\uc0c9\ucc3d\uc744 \uc5f4\uc5c8",
                        "\uac80\uc0c9\ucc3d\uc744 \uc5f4\uc5c8\ub2e4\uac00"
                )
                && containsAny(
                        analysisText,
                        "\uba87 \uae00\uc790\ub97c \uc37c\ub2e4\uac00",
                        "\uba87 \uae00\uc790\ub97c \uc37c",
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uba87 \uae00\uc790\uae4c\uc9c0 \uc785\ub825",
                        "\uba87 \uae00\uc790\uae4c\uc9c0 \uc785\ub825"
                )
                && containsAny(
                        analysisText,
                        "\uc9c0\uc6e0",
                        "\uc785\ub825\ud558\uace0 \uc9c0\uc6e0"
                );

        boolean thirdPartyTriggerSelfPartialSearchInputThenSelfStopped =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \uac11\uc790\uae30 \uc0dd\uac01\ub098"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9\ucc3d\uc744 \uc5f4"
                )
                && containsAny(
                        analysisText,
                        "\uba87 \uae00\uc790 \uce58\ub2e4\uac00",
                        "\uba87 \uae00\uc790 \uce58"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ub2eb\uc558",
                        "\ub2eb\uc558"
                );

        boolean bettingIntentPartialSearchInputExternalDistraction =
                containsAny(
                        analysisText,
                        "\ubca0\ud305\ud560\uae4c \uc2f6\uc5b4\uc11c",
                        "\ubca0\ud305\ud560\uae4c \uc2f6\uc5c8"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9\ucc3d\uc744 \uc5f4\uc5c8",
                        "\uac80\uc0c9\ucc3d\uc744 \uc5f4"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9\uc5b4\ub3c4 \ub05d\uae4c\uc9c0 \uc548 \ucce4",
                        "\uac80\uc0c9\uc5b4\ub97c \ub05d\uae4c\uc9c0 \uc548 \ucce4"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\ub978 \uc77c \ub54c\ubb38\uc5d0 \uc78a\uc5b4\ubc84\ub838",
                        "\ub2e4\ub978 \uc77c \ub54c\ubb38\uc5d0 \uc78a\uc5b4"
                );
        boolean siteNameInputCompleteThenOtherTaskSelfStopped =
                containsAny(
                        analysisText,
                        "\ucd95\uad6c \uacb0\uacfc"
                )
                && containsAny(
                        analysisText,
                        "\uc608\uc804 \uc0dd\uac01\uc774 \ub098"
                )
                && containsAny(
                        analysisText,
                        "\uc2e4\uc81c\ub85c \ud558\uc9c0\ub294 \uc54a\uc558"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9\ucc3d\uc5d0 \uc0ac\uc774\ud2b8 \uc774\ub984\ub9cc \uc801\uc5b4\ubd24"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\ub978 \uc77c\uc744 \ud588"
                );

        boolean thirdPartySportsOddsSelfPageAccessThenSelfStopped =
                thirdPartySportsOddsSelfPageAccessAttempt
                && containsAny(
                        analysisText,
                        "\uc544\uc774\ub514\ub098 \ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\uc9c0 \uc54a\uace0",
                        "\uc544\uc774\ub514\ub098 \ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\uc9c0 \uc54a"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ub098\uc654",
                        "\uadf8\ub0e5 \ub098\uc628"
                );

        boolean loginScreenReachedThenPhonePutDown =
                containsLoginScreenEntryAttempt(analysisText)
                && containsAny(
                        analysisText,
                        "\ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub193",
                        "\ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub1a8"
                );

        boolean loginScreenReachedThenBrowserClosed =
                containsLoginScreenEntryAttempt(analysisText)
                && containsAny(
                        analysisText,
                        "\ube0c\ub77c\uc6b0\uc800\ub97c \ub2eb\uc558",
                        "\ube0c\ub77c\uc6b0\uc800\ub97c \ub2eb"
                );

        boolean loginScreenPasswordEntryConsideredThenSelfStopped =
                containsLoginScreenEntryAttempt(analysisText)
                && containsAny(
                        analysisText,
                        "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\ub824\ub2c8",
                        "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\ub824\uace0"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ub2eb\uc558",
                        "\uadf8\ub0e5 \ub2eb\uc558\uc2b5\ub2c8\ub2e4",
                        "\ud654\uba74\uc744 \ub2eb\uc558"
                );

        boolean selfSiteAccessLoginIdInputPasswordNotEnteredThenSelfStopped =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\ub3c4\ubc15"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9 \uacb0\uacfc\ub97c \ud655\uc778",
                        "\uac80\uc0c9\uacb0\uacfc\ub97c \ud655\uc778",
                        "\uc0ac\uc774\ud2b8\uc5d0\ub3c4 \ub4e4\uc5b4\uac14",
                        "\uc0ac\uc774\ud2b8\uc5d0 \ub4e4\uc5b4\uac14"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778 \ud654\uba74",
                        "\ub85c\uadf8\uc778\ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\uc608\uc804 \uc544\uc774\ub514\ub97c \uc785\ub825",
                        "\uc544\uc774\ub514\ub97c \uc785\ub825"
                )
                && containsAny(
                        analysisText,
                        "\ube44\ubc00\ubc88\ud638\ub294 \ub123\uc9c0 \uc54a",
                        "\ube44\ubc00\ubc88\ud638\ub97c \ub123\uc9c0 \uc54a",
                        "\ube44\ubc00\ubc88\ud638\ub294 \uc785\ub825\ud558\uc9c0 \uc54a"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ud654\uba74\uc744 \uc7a0\uae50 \ubcf4\uace0 \ub098\uc654",
                        "\ud654\uba74\uc744 \uc7a0\uae50 \ubcf4\uace0 \ub098\uc654",
                        "\ubcf4\uace0 \ub098\uc654"
                );

        boolean thirdPartyTriggerSelfSiteAccessLoginIdInputThenSelfStopped =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8\uc5d0 \ub4e4\uc5b4\uac14",
                        "\uc0ac\uc774\ud2b8\uc5d0 \ub4e4\uc5b4\uac00",
                        "\uc0ac\uc774\ud2b8\ub85c \ub4e4\uc5b4\uac14",
                        "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \uac14"
                )
                && containsAny(
                        analysisText,
                        "\uc544\uc774\ub514\ub9cc \uc801\uc5b4\ubd24",
                        "\uc544\uc774\ub514\ub9cc \uc801\uc5b4",
                        "\uc544\uc774\ub514\ub97c \uc801\uc5b4\ubd24",
                        "\uc608\uc804 \uc544\uc774\ub514\ub97c \uc785\ub825",
                        "\uc544\uc774\ub514\ub97c \uc785\ub825"
                )
                && containsAny(
                        analysisText,
                        "\uadf8 \uc774\uc0c1\uc740 \ud558\uc9c0 \uc54a\uc558",
                        "\uadf8 \uc774\uc0c1 \ud558\uc9c0 \uc54a\uc558",
                        "\ub354 \uc9c4\ud589\ud558\uc9c0 \uc54a\uace0 \ub2eb",
                        "\ub354 \uc9c4\ud589\ud558\uc9c0 \uc54a"
                );

        boolean casinoSiteNameSearchLoginScreenReachedAttempt =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uac80\uc0c9",
                        "\uc0ac\uc774\ud2b8\uc774\ub984\uc744 \uac80\uc0c9"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9 \uacb0\uacfc",
                        "\ube44\uc2b7\ud55c \uc8fc\uc18c"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ud655\uc778",
                        "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0",
                        "\ub85c\uadf8\uc778 \ud654\uba74"
                );

        boolean thirdPartyCasinoTriggerSelfSearchLoginScreenThenSelfStopped =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \ud55c \ubc88 \ucc3e\uc544\ubd24",
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544\ubd24",
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9",
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uac80\uc0c9",
                        "\uac80\uc0c9 \uacb0\uacfc\uc5d0\uc11c \ube44\uc2b7\ud55c \uc8fc\uc18c"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac00",
                        "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac14",
                        "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ud655\uc778",
                        "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0",
                        "\ub85c\uadf8\uc778 \ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ub2eb",
                        "\uadf8\ub0e5 \ucc3d\uc744 \ub2eb",
                        "\ucc3d\uc744 \ub2eb",
                        "\uacc4\uc815\uc774 \uc0b4\uc544 \uc788\ub294\uc9c0\ub294 \ud655\uc778\ud558\uc9c0 \uc54a"
                );

        boolean lateDomainCasinoAccessThenSelfStopped =
                selfContextExtracted
                && containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8\uc778 \uac74 \ub4e4\uc5b4\uac00\uace0 \ub098\uc11c \uc54c\uc558",
                        "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8\uc778 \uac83\uc740 \ub4e4\uc5b4\uac00\uace0 \ub098\uc11c \uc54c\uc558",
                        "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8"
                )
                && containsAny(
                        analysisText,
                        "\ud654\uba74 \uc870\uae08 \ubcf4\uace0",
                        "\ud654\uba74\uc744 \uc870\uae08 \ubcf4\uace0",
                        "\ud654\uba74 \uc7a0\uae50 \ubcf4\uace0"
                )
                && containsAny(
                        analysisText,
                        "\ubc14\ub85c \uaed0",
                        "\ubc14\ub85c \uaed0\uc5b4",
                        "\uaed0\uc5b4"
                );

        boolean thirdPartyLinkCasinoAccessThenSelfStopped =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\ub20c\ub800\ub294\ub370",
                        "\ub20c\ub800"
                )
                && containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178 \ud654\uba74\uc774 \ub098\uc624",
                        "\uce74\uc9c0\ub178 \ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\uc7a0\uae50 \ub458\ub7ec\ubcf4\ub2e4\uac00",
                        "\uc7a0\uae50 \ub458\ub7ec\ubd24"
                )
                && containsAny(
                        analysisText,
                        "\ubc14\ub85c \uaed0",
                        "\uaed0\uc2b5\ub2c8\ub2e4"
                );

        boolean relatedAppViewedThenDeleted =
                containsAny(
                        analysisText,
                        "\uc608\uc804 \uc2b5\uad00\uc73c\ub85c \ub3cc\uc544\uac08\uae4c \ubd10 \uac71\uc815",
                        "\uc608\uc804 \uc2b5\uad00\uc73c\ub85c \ub3cc\uc544\uac08\uae4c"
                )
                && containsAny(
                        analysisText,
                        "\uad00\ub828 \uc571\uc744 \ucc3e\uc544\uc11c",
                        "\uad00\ub828 \uc571\uc744 \ucc3e\uc558"
                )
                && containsAny(
                        analysisText,
                        "\ud654\uba74\ub9cc \uc7a0\uae50 \ubd24",
                        "\ud654\uba74\uc744 \uc7a0\uae50 \ubd24"
                )
                && containsAny(
                        analysisText,
                        "\uc0ad\uc81c\ud588",
                        "\uc571\uc744 \uc9c0\uc6e0"
                )
                && containsAny(
                        analysisText,
                        "\uc2e4\uc81c\ub85c \ub3c8\uc744 \ub123\uac70\ub098 \ubca0\ud305\ud55c \uc801\uc740 \uc5c6",
                        "\ub3c8\uc744 \ub123\uac70\ub098 \ubca0\ud305\ud55c \uc801\uc740 \uc5c6"
                );

        boolean relatedAppPresenceSearchThenSelfStopped =

                containsAny(

                        analysisText,

                        "\uc608\uc804 \uc77c\uc774 \uc7a0\uae50 \uc0dd\uac01\ub0ac"

                )

                && containsAny(

                        analysisText,

                        "\uad00\ub828 \uc571\uc774 \uc544\uc9c1 \ub0a8\uc544 \uc788\ub294\uc9c0\ub3c4 \ucc3e\uc544\ubd24"

                )

                && containsAny(

                        analysisText,

                        "\ud654\uba74\uc5d0 \ubcf4\uc774\ub294 \uc774\ub984\ub9cc \ud655\uc778"

                )

                && containsAny(

                        analysisText,

                        "\uc5f4\uc5b4\ubcf4\uc9c0\ub294 \uc54a\uc558"

                );

        boolean existingSlotAppLoggedInGameScreenThenSelfStopped =
                containsAny(
                        analysisText,
                        "\uc2ac\ub86f",
                        "\uc2ac\ub86f \uc571",
                        "\uc2ac\ub86f\uc571"
                )
                && containsAny(
                        analysisText,
                        "\uc571\uc774 \uc544\uc9c1 \ud734\ub300\ud3f0\uc5d0 \ub0a8\uc544",
                        "\uc571\uc774 \uc544\uc9c1 \ub0a8\uc544",
                        "\uadf8 \uc571\uc774 \uc544\uc9c1"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ub20c\ub7ec\ubd24",
                        "\ub20c\ub7ec\ubd24"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\uc740 \uc774\ubbf8 \ub418\uc5b4",
                        "\ub85c\uadf8\uc778\uc774 \uc774\ubbf8 \ub418\uc5b4",
                        "\uc774\ubbf8 \ub85c\uadf8\uc778"
                )
                && containsAny(
                        analysisText,
                        "\uac8c\uc784 \ud654\uba74\uae4c\uc9c0 \ubc14\ub85c \ub4e4\uc5b4\uac14",
                        "\uac8c\uc784 \ud654\uba74\uae4c\uc9c0",
                        "\uac8c\uc784 \ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\uc571\uc744 \uc885\ub8cc",
                        "\uc571\uc744 \uaed0",
                        "\uc885\ub8cc\ud588"
                )
                && containsAny(
                        analysisText,
                        "\ub3c8\uc744 \ub123\uac70\ub098 \uac8c\uc784\uc744 \ub3cc\ub9ac\uc9c0\ub294 \uc54a",
                        "\ub3c8\uc744 \ub123\uc9c0 \uc54a",
                        "\uac8c\uc784\uc744 \ub3cc\ub9ac\uc9c0\ub294 \uc54a"
                );

        boolean relatedInterfaceReachedThenSelfStopped =
                containsAny(
                        analysisText,
                        "\ud558\uace0 \uc2f6\uc740 \ub9c8\uc74c\uc774 \uc62c\ub77c\uc624"
                )
                && containsAny(
                        analysisText,
                        "\uad00\ub828 \ud654\uba74\uae4c\uc9c0 \uac14"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08\ud558\ub824\uace0 \ud588\ub358 \uac74 \uc544\ub2c8",
                        "\uc785\uae08\ud558\ub824\uace0 \ud588\ub358 \uac83\uc740 \uc544\ub2c8"
                )
                && containsAny(
                        analysisText,
                        "\ud654\uba74\uc744 \ub2eb\uc558",
                        "\ud654\uba74\uc744 \ub2eb"
                );

        boolean fundingScreenReachedThenSelfStopped =
                containsAny(
                        analysisText,
                        "\ubca0\ud305",
                        "\ub3c4\ubc15",
                        "\uc2ac\ub86f",
                        "\uce74\uc9c0\ub178"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud588",
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \ud654\uba74\uae4c\uc9c0\ub294 \uac14",
                        "\uc785\uae08 \ud654\uba74\uae4c\uc9c0 \uac14",
                        "\uc785\uae08 \ud654\uba74\uc5d0 \uac14"
                )
                && containsAny(
                        analysisText,
                        "\uc2e4\uc81c \uc785\uae08\uc740 \uc548 \ud588",
                        "\uc2e4\uc81c \uc785\uae08\uc740 \ud558\uc9c0 \uc54a",
                        "\uc785\uae08\uc740 \uc548 \ud588"
                )
                && containsAny(
                        analysisText,
                        "\uac70\uae30\uc11c \uba48\ucdc4",
                        "\uac70\uae30\uc11c \uba48\ucd94",
                        "\uadf8\ub0e5 \uaed0"
                );

        boolean loginCompletedFundingScreenThenSelfExited =
                containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\uae4c\uc9c0\ub294 \ud574\ubc84\ub838",
                        "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud574\ubc84\ub838",
                        "\ub85c\uadf8\uc778\uae4c\uc9c0\ub294 \ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \ud654\uba74\uc744 \ubcf4\ub2e4\uac00",
                        "\uc785\uae08 \ud654\uba74\uc744 \ubcf4\uace0",
                        "\uc785\uae08 \ud654\uba74\uc5d0\uc11c"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ub098\uc654",
                        "\uadf8\ub0e5 \ub098\uc628",
                        "\ud654\uba74\uc5d0\uc11c \ub098\uc654"
                );

        boolean gamblingSiteResultViewThenSelfExited =

                containsAny(

                        analysisText,

                        "\ubca0\ud305\uc744 \ud558\ub824\uace0 \ub4e4\uc5b4\uac04 \uac74 \uc544\ub2c8"

                )

                && containsAny(

                        analysisText,

                        "\uacbd\uae30 \uacb0\uacfc\uac00 \uad81\uae08\ud574\uc11c \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d\ud588",
                        "\uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d\ud588"

                )

                && containsAny(

                        analysisText,

                        "\ubc30\ub2f9\uc774 \uac19\uc774 \ubcf4\uc774"

                )

                && containsAny(

                        analysisText,

                        "\ud55c\ucc38 \uc804\uc5d0\ub294 \uac70\uae30\uc5d0 \ub3c8\uc744 \uac78",
                        "\uac70\uae30\uc5d0 \ub3c8\uc744 \uac78\uae30\ub3c4 \ud588"

                )

                && containsAny(

                        analysisText,

                        "\uacb0\uacfc\ub9cc \ubcf4\uace0 \ub098\uc654",
                        "\uacb0\uacfc\ub9cc \ubcf4\uace0 \ub098\uc624"

                );

        boolean sportsMultiWagerNextDayUrgeMondayAppDeleted =
                containsAny(
                        analysisText,
                        "\uc2a4\ud3ec\uce20 \uacbd\uae30",
                        "\uc2a4\ud3ec\uce20\uacbd\uae30"
                )
                && containsAny(
                        analysisText,
                        "\uc18c\uc561\uc73c\ub85c \ubca0\ud305\uc744 \ud588",
                        "\ubca0\ud305\uc744 \ud588\uc2b5\ub2c8\ub2e4"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\uc74c \uacbd\uae30\uc5d0\ub3c4 \uae08\uc561\uc744 \uc870\uae08 \ub123",
                        "\ub2e4\uc74c \uacbd\uae30\uc5d0\ub3c4 \uae08\uc561"
                )
                && containsAny(
                        analysisText,
                        "\uc5ec\ub7ec \uacbd\uae30\ub97c \ubb36\uc5b4\uc11c \uc81c\ucd9c",
                        "\uc5ec\ub7ec \uacbd\uae30\ub97c \ubb36\uc5b4\uc11c"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\uc74c \ub0a0\uc5d0\ub294 \ub2e4\uc2dc \ud560\uae4c",
                        "\ub2e4\uc2dc \ud560\uae4c \uc7a0\uae50 \uc0dd\uac01"
                )
                && containsAny(
                        analysisText,
                        "\uc2e4\uc81c\ub85c \uc571\uc744 \ucf1c\uc9c0\ub294 \uc54a",
                        "\uc571\uc744 \ucf1c\uc9c0\ub294 \uc54a"
                )
                && containsAny(
                        analysisText,
                        "\uc6d4\uc694\uc77c\uc5d0\ub294 \uc571\uc744 \uc0ad\uc81c",
                        "\uc6d4\uc694\uc77c\uc5d0 \uc571\uc744 \uc0ad\uc81c"
                );

        boolean completedWagerNextMorningAppDeletedRelapseRecovery =
                containsAny(
                        analysisText,
                        "어젯밤에 실제로 베팅을 했고",
                        "어젯밤 실제로 베팅을 했",
                        "어젯밤에 실제로 베팅했"
                )
                && containsAny(
                        analysisText,
                        "오늘 아침에 앱을 지웠",
                        "오늘 아침 앱을 지웠",
                        "오늘 아침에 앱을 삭제했",
                        "오늘 아침 앱을 삭제했"
                );

        boolean wagerCompletedThenNextDayAppDeletedRecovery =
                (
                        containsAny(
                                analysisText,
                                "\uc608\uc804 \uacc4\uc815\uc73c\ub85c \ub85c\uadf8\uc778\ud588\uace0"
                        )
                        && containsAny(
                                analysisText,
                                "\uc18c\uc561\uc73c\ub85c \ud55c \ubc88 \uac78\uc5c8"
                        )
                        && containsAny(
                                analysisText,
                                "\ub2e4\uc74c \ub0a0 \uc571\uc740 \uc9c0\uc6e0",
                                "\ub2e4\uc74c \ub0a0 \uc571\uc744 \uc9c0\uc6e0"
                        )
                )
                || (
                        containsAny(
                                analysisText,
                                "\uacb0\uad6d \ubca0\ud305\uc744 \ud588\uc2b5\ub2c8\ub2e4",
                                "\uacb0\uad6d \ubca0\ud305\uc744 \ud588",
                                "\ubca0\ud305\uc744 \ud588\uc2b5\ub2c8\ub2e4"
                        )
                        && containsAny(
                                analysisText,
                                "\uae08\uc561\uc744 \uc785\ub825\ud574\uc11c \uc2e4\uc81c\ub85c \ub123\uc5c8",
                                "\uae08\uc561\uc744 \uc785\ub825\ud574\uc11c",
                                "\uc2e4\uc81c\ub85c \ub123\uc5c8"
                        )
                        && containsAny(
                                analysisText,
                                "\ub2e4\uc74c \ub0a0\uc5d0\ub294 \uc571\uc744 \uc0ad\uc81c",
                                "\ub2e4\uc74c \ub0a0 \uc571\uc744 \uc0ad\uc81c",
                                "\ub2e4\uc74c \ub0a0\uc5d0\ub294 \uc571\uc744 \uc9c0\uc6e0"
                        )
                )
                || (
                        containsAny(
                                analysisText,
                                "\ubca0\ud305 \uc571\uc744 \ub2e4\uc2dc \uc124\uce58",
                                "\ubca0\ud305\uc571\uc744 \ub2e4\uc2dc \uc124\uce58",
                                "\ubca0\ud305 \uc571",
                                "\ubc30\ud305 \uc571\uc744 \ub2e4\uc2dc \uc124\uce58",
                                "\ubc30\ud305\uc571\uc744 \ub2e4\uc2dc \uc124\uce58",
                                "\ubc30\ud305 \uc571"
                        )
                        && containsAny(
                                analysisText,
                                "\ubca0\ud305\uc744 \uc644\ub8cc",
                                "\ubca0\ud305\uc744 \uc644\ub8cc\ud588"
                        )
                        && containsAny(
                                analysisText,
                                "\ub2e4\uc74c \uacbd\uae30\uae4c\uc9c0 \ucd94\uac00\ub85c \uac78",
                                "\ucd94\uac00\ub85c \uac78"
                        )
                        && containsAny(
                                analysisText,
                                "\ub2e4\uc74c \ub0a0 \uc190\uc2e4\uc744 \ud655\uc778",
                                "\ub2e4\uc74c \ub0a0"
                        )
                        && containsAny(
                                analysisText,
                                "\uadf8\ub0a0 \ubc14\ub85c \uc571\uc744 \uc9c0\uc6e0",
                                "\uadf8\ub0a0 \uc571\uc744 \uc9c0\uc6e0",
                                "\uc571\uc744 \uc9c0\uc6e0"
                        )
                );

        boolean sportsBettingSearchAutocompleteThenSelfStopped =
                (
                        containsAny(
                                analysisText,
                                "\uac80\uc0c9\ucc3d\uc5d0 \uc2a4\ud3ec\uce20\ubca0\ud305\uc774\ub77c\uace0 \uce58\uae34 \ud588",
                                "\uc2a4\ud3ec\uce20\ubca0\ud305\uc774\ub77c\uace0 \uce58\uae34 \ud588"
                        )
                        && containsAny(
                                analysisText,
                                "\uc790\ub3d9\uc644\uc131\uc73c\ub85c \uc608\uc804\uc5d0 \uc4f0\ub358 \uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub728",
                                "\uc608\uc804\uc5d0 \uc4f0\ub358 \uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub728"
                        )
                        && containsAny(
                                analysisText,
                                "\ub354 \uc548 \ub20c\ub800"
                        )
                )
                || (
                        containsAny(
                                analysisText,
                                "\uc2a4\ud3ec\uce20\ubca0\ud305 \uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \uac11\uc790\uae30 \ub5a0\uc62c\ub77c",
                                "\uc2a4\ud3ec\uce20\ubca0\ud305 \uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \ub5a0\uc62c\ub77c"
                        )
                        && containsAny(
                                analysisText,
                                "\uac80\uc0c9\ucc3d\uc5d0 \uba87 \uae00\uc790 \uccd0\ubd24",
                                "\uac80\uc0c9\ucc3d\uc5d0 \uba87 \uae00\uc790 \uccd0 \ubd24"
                        )
                        && containsAny(
                                analysisText,
                                "\uc790\ub3d9\uc644\uc131\ub9cc \ubcf4\uace0",
                                "\uc790\ub3d9\uc644\uc131\uc744 \ubcf4\uace0"
                        )
                        && containsAny(
                                analysisText,
                                "\uadf8\ub0e5 \ub2eb\uc558",
                                "\uac80\uc0c9\ucc3d\uc744 \uadf8\ub0e5 \ub2eb\uc558"
                        )
                );
        boolean pastSportsBettingSiteSearchResultViewedThenSelfStopped =
                containsAny(
                        analysisText,
                        "\uc608\uc804\uc5d0 \ud558\ub358 \uc2a4\ud3ec\uce20\ubca0\ud305 \uc0ac\uc774\ud2b8",
                        "스포츠베팅 사이트",
                        "\uc608\uc804\uc5d0 \ud558\ub358 \uc2a4\ud3ec\uce20\ubca0\ud305"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9\ub418\ub294\uc9c0 \uadf8\ub0e5 \ucc3e\uc544\ubd24",
                        "스포츠베팅 사이트를 잠깐 찾아봤",
                        "\uadf8\ub0e5 \ucc3e\uc544\ubd24"
                )
                && containsAny(
                        analysisText,
                        "\uacb0\uacfc \uba87 \uac1c \ubcf4\uace0",
                        "\uacb0\uacfc\ub97c \ubd24",
                        "검색 결과만 조금 확인"
                )
                && containsAny(
                        analysisText,
                        "\ub354 \ub4e4\uc5b4\uac00\uc9c4 \uc54a\uc558",
                        "\ub354 \ub4e4\uc5b4\uac00\uc9c0 \uc54a\uc558",
                        "휴대폰을 내려놨"
                );

        boolean bettingAmountViewedThenSelfStopped =

                containsAny(

                        analysisText,

                        "\uacbd\uae30 \ud558\ub098 \ubcf4\uace0"

                )

                && containsAny(

                        analysisText,

                        "\ubca0\ud305 \uae08\uc561\ub3c4 \ud655\uc778",
                        "\ubca0\ud305 \uae08\uc561\uc744 \ud655\uc778"

                )

                && containsAny(

                        analysisText,

                        "\uc2e4\uc81c\ub85c \ub3c8\uc744 \uac78\uc9c0\ub294 \uc54a\uc558",
                        "\uc2e4\uc81c\ub85c \ub3c8\uc744 \uac78\uc9c0 \uc54a\uc558"

                )

                && containsAny(

                        analysisText,

                        "\uacb0\uacfc\ub9cc \ubcf4\uace0 \ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub1a8",
                        "\uacb0\uacfc\ub9cc \ubcf4\uace0 \ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub1a8\uc5b4\uc694"

                );

        boolean slotAppReinstallLoginFundingAmountDeleteSelfStop =
                containsAny(
                        analysisText,
                        "\uc2ac\ub86f",
                        "\uc2ac\ub86f \uc571"
                )
                && containsAny(
                        analysisText,
                        "\uc2a4\ud1a0\uc5b4\uc5d0\uc11c \ub2e4\uc2dc \uc124\uce58",
                        "\ub2e4\uc2dc \uc124\uce58",
                        "\uc7ac\uc124\uce58"
                )
                && containsAny(
                        analysisText,
                        "\uc2e4\ud589\ud574\uc11c \ub85c\uadf8\uc778",
                        "\uc2e4\ud589\ud558\uace0 \ub85c\uadf8\uc778",
                        "\ub85c\uadf8\uc778\ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \ud654\uba74",
                        "\uc785\uae08\ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc6a9\ud560 \uae08\uc561\uae4c\uc9c0 \uc801\uc5c8",
                        "\uae08\uc561\uae4c\uc9c0 \uc801\uc5c8",
                        "\uae08\uc561\uc744 \uc785\ub825"
                )
                && containsAny(
                        analysisText,
                        "\ub9c8\uc74c\uc774 \ubc14\ub010",
                        "\ub9c8\uc74c\uc774 \ubc14\ub00c"
                )
                && containsAny(
                        analysisText,
                        "\uc785\ub825\ud55c \uae08\uc561\uc744 \uc9c0\uc6b0",
                        "\uae08\uc561\uc744 \uc9c0\uc6b0",
                        "\uae08\uc561\uc744 \uc9c0\uc6e0"
                );

        boolean appReinstallLoginFundingScreenThenSelfStopped =

                containsAny(

                        analysisText,

                        "\uc608\uc804 \uc571\uc744 \ub2e4\uc2dc \uc124\uce58\ud588",
                        "\uc608\uc804 \uc571\uc744 \ub2e4\uc2dc \uc124\uce58"

                )

                && containsAny(

                        analysisText,

                        "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud558\uace0",
                        "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588"

                )

                && containsAny(

                        analysisText,

                        "\uc785\uae08 \ud654\uba74\ub3c4 \ubd24",
                        "\uc785\uae08 \ud654\uba74\uc744 \ubd24"

                )

                && containsAny(

                        analysisText,

                        "\uae08\uc561\uc744 \uc785\ub825\ud558\uc9c0\ub294 \uc54a\uc558",
                        "\uae08\uc561\uc740 \uc785\ub825\ud558\uc9c0 \uc54a\uc558"

                )

                && containsAny(

                        analysisText,

                        "\uadf8\ub0e5 \ud654\uba74 \ub2eb\uace0",
                        "\ud654\uba74\uc744 \ub2eb\uace0",
                        "\ud654\uba74 \ub2eb\uace0"

                );

        boolean wagerAmountInputDeletedThenDeviceHandoffRecovery =

                containsAny(

                        analysisText,

                        "\ubca0\ud305\ud558\ub824\uace0"

                )

                && containsAny(

                        analysisText,

                        "\uae08\uc561\uc744 \uc870\uae08 \uc801\uc5b4\ub1a8\ub2e4\uac00",
                        "\uae08\uc561\uc744 \uc801\uc5b4\ub1a8\ub2e4\uac00"

                )

                && containsAny(

                        analysisText,

                        "\uc9c0\uc6e0\uc5b4\uc694",
                        "\uc9c0\uc6e0"

                )

                && containsAny(

                        analysisText,

                        "\uacc4\uc18d \uc190\uc774 \uac00\uc11c"

                )

                && containsAny(

                        analysisText,

                        "\uac00\uc871\ud55c\ud14c \ud734\ub300\ud3f0\uc744 \uc7a0\uae50 \ub9e1\uacbc\uc2b5\ub2c8\ub2e4",
                        "\uac00\uc871\ud55c\ud14c \ud734\ub300\ud3f0\uc744 \uc7a0\uae50 \ub9e1\uacbc"

                );

        boolean sportsBettingAmountInputSiblingInterruptedAttempt =
                containsAny(
                        analysisText,
                        "\uc2a4\ud3ec\uce20\ubca0\ud305 \uc0ac\uc774\ud2b8",
                        "\uc2a4\ud3ec\uce20 \ubca0\ud305 \uc0ac\uc774\ud2b8"
                )
                && containsAny(
                        analysisText,
                        "\uc8fc\uc18c\ub97c \uc9c1\uc811 \uc785\ub825",
                        "\uc811\uc18d\uc740 \ub410"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\ub3c4 \uc815\uc0c1\uc801\uc73c\ub85c \uc644\ub8cc",
                        "\ub85c\uadf8\uc778\uc774 \uc815\uc0c1\uc801\uc73c\ub85c \uc644\ub8cc"
                )
                && containsAny(
                        analysisText,
                        "\ubc30\ub2f9\uc744 \uc120\ud0dd\ud558\uace0 \uae08\uc561\uc744 \uc785\ub825",
                        "\uae08\uc561\uc744 \uc785\ub825\ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc81c\ucd9c \ubc84\ud2bc\uc744 \ub204\ub974\uae30 \uc9c1\uc804",
                        "\uc81c\ucd9c \uc9c1\uc804"
                )
                && containsAny(
                        analysisText,
                        "\ub3d9\uc0dd\uc774 \ubc29\uc5d0 \ub4e4\uc5b4\uc640",
                        "\ub3d9\uc0dd\uc774"
                )
                && containsAny(
                        analysisText,
                        "\ucef4\ud4e8\ud130\ub97c \uae09\ud558\uac8c \ub2eb",
                        "\uae09\ud558\uac8c \ub2eb"
                );

        boolean bettingAppAmountInputPhoneInterruptedAttempt =
                containsAny(
                        analysisText,
                        "베팅 앱을 오늘 다시 실행",
                        "베팅 앱을 다시 실행"
                )
                && containsAny(
                        analysisText,
                        "금액을 입력하는 데까지 갔",
                        "금액 입력하는 데까지 갔",
                        "금액을 입력하는 데까지"
                )
                && containsAny(
                        analysisText,
                        "전화가 와서 중단",
                        "전화가 와서"
                );

        boolean wagerAmountInputPhoneInterruptedNextDaySelfExited =
                containsAny(
                        analysisText,
                        "베팅 금액까지 적어놓고",
                        "베팅 금액을 적어놓고",
                        "베팅 금액까지 적었"
                )
                && containsAny(
                        analysisText,
                        "전화가 와서 끊겼",
                        "전화가 와서"
                )
                && containsAny(
                        analysisText,
                        "다음날 보니까 그대로",
                        "다음 날 보니까 그대로"
                )
                && containsAny(
                        analysisText,
                        "결국 아무것도 안 하고 나왔",
                        "아무것도 안 하고 나왔"
                );

        boolean wagerAmountInputDeletedThenSelfExited =
                containsAny(
                        analysisText,
                        "베팅 화면까지 넘어가",
                        "베팅 화면까지 들어갔",
                        "베팅 화면까지 들어가",
                        "베팅 사이트까지 들어갔",
                        "베팅 사이트까지 들어가"
                )
                && containsAny(
                        analysisText,
                        "금액 칸에 숫자를 조금 적었다가",
                        "금액 칸에 숫자를 적었다가",
                        "금액을 조금 적었다가",
                        "금액을 적었다가",
                        "금액을 입력했다가"
                )
                && containsAny(
                        analysisText,
                        "지우고",
                        "지웠"
                )
                && containsAny(
                        analysisText,
                        "그냥 화면을 닫았",
                        "화면을 닫았",
                        "화면을 닫고",
                        "그 다음은 하지 않았"
                );

        boolean bettingAppAmountInputUncertainSubmitSelfCancel =
                containsAny(
                        analysisText,
                        "\uacbd\uae30 \ubca0\ud305 \uc571",
                        "\ubca0\ud305 \uc571"
                )
                && containsAny(
                        analysisText,
                        "\uae08\uc561\ub3c4 \uc785\ub825\ud588",
                        "\uae08\uc561\uc744 \uc785\ub825\ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc81c\ucd9c \uc9c1\uc804\uc5d0 \ub9c8\uc74c\uc774 \ubc14\ub00c\uc5b4\uc11c \ucde8\uc18c",
                        "\ub9c8\uc74c\uc774 \ubc14\ub00c\uc5b4\uc11c \ucde8\uc18c\ud558\ub824"
                )
                && containsAny(
                        analysisText,
                        "\ub20c\ub978 \uac83 \uac19\uae30\ub3c4 \ud558\uace0 \uc544\ub2cc \uac83 \uac19\uae30\ub3c4",
                        "\ubc84\ud2bc\uc744 \ub20c\ub978 \uac83 \uac19\uae30\ub3c4",
                        "\uc190\uc774 \ubbf8\ub044\ub7ec\uc838"
                )
                && containsAny(
                        analysisText,
                        "\uc794\uc561\uc5d0\ub294 \ubcc0\ud654\uac00 \uc5c6\ub294 \uac83 \uac19",
                        "\uc794\uc561\uc5d0 \ubcc0\ud654\uac00 \uc5c6\ub294 \uac83 \uac19"
                )
                && containsAny(
                        analysisText,
                        "\uc571\uc744 \uaed0",
                        "\uc571\uc744 \uaed0\uc2b5\ub2c8\ub2e4"
                );

        boolean wagerScreenReachedBeforeAmountInputThenSelfExited =
                containsAny(
                        analysisText,
                        "사이트에 접속해서 베팅 화면까지 갔",
                        "베팅 화면까지 갔"
                )
                && containsAny(
                        analysisText,
                        "금액을 넣기 전에",
                        "금액 입력 전에"
                )
                && containsAny(
                        analysisText,
                        "그냥 껐",
                        "화면을 껐"
                )
                && containsAny(
                        analysisText,
                        "그 뒤로는 다시 안 열어봤",
                        "다시 안 열어봤"
                );

        boolean wagerScreenReachedMotivationDropThenSelfExited =

                containsAny(

                        analysisText,

                        "\ubca0\ud305 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac14",
                        "\ubca0\ud305 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac00"

                )

                && containsAny(

                        analysisText,

                        "\ud558\uae30 \uc2eb\uc5b4\uc84c",
                        "\ud558\uae30 \uc2eb\uc5b4\uc838"

                )

                && containsAny(

                        analysisText,

                        "\uadf8\ub0e5 \uc885\ub8cc\ud558\uace0 \ub098\uc654",
                        "\uc885\ub8cc\ud558\uace0 \ub098\uc654"

                );

        boolean completedFundingSingleWagerPostNoActionRelapse =
                containsAny(
                        analysisText,
                        "실제로 돈을 넣고",
                        "실제 돈을 넣고"
                )
                && containsAny(
                        analysisText,
                        "한 번 걸었습니다",
                        "한 번 걸었"
                )
                && containsAny(
                        analysisText,
                        "그 뒤에는 따로 뭘 하진 않았",
                        "그 뒤에는 따로 뭘 하지 않았",
                        "그 뒤로 따로 뭘 하진 않았"
                );

        boolean wagerCompletedRelapseThenNextDayNoAction =
                containsAny(
                        analysisText,
                        "다시 해버렸습니다",
                        "다시 해버렸"
                )
                && containsAny(
                        analysisText,
                        "베팅 금액 넣고",
                        "베팅 금액을 넣고",
                        "금액 넣고"
                )
                && containsAny(
                        analysisText,
                        "버튼까지 눌렀는데",
                        "버튼까지 눌렀"
                )
                && containsAny(
                        analysisText,
                        "끝나고 나니까 후회",
                        "끝나고 나니 후회"
                )
                && containsAny(
                        analysisText,
                        "오늘은 아직 아무것도 안 하고",
                        "오늘은 아무것도 안 하고"
                );

        boolean appReinstalledAndExecutedWithUrgeNegation =
                containsAny(
                        analysisText,
                        "앱을 찾았습니다",
                        "앱을 찾았"
                )
                && containsAny(
                        analysisText,
                        "재설치까지 하고",
                        "재설치했",
                        "다시 설치했"
                )
                && containsAny(
                        analysisText,
                        "실행했는데",
                        "실행까지 했",
                        "실행했"
                )
                && containsAny(
                        analysisText,
                        "별로 하고 싶은 마음은 안 들어"
                );

        boolean currentOccasionalGamblingThought =
                currentContextExtracted
                && priorGamblingContextForCurrentThought
                && containsAny(
                        analysisText,
                        "\uc0dd\uac01\uc774 \uac00\ub054 \ub09c",
                        "\uac00\ub054 \uc0dd\uac01\uc774 \ub09c",
                        "\uc0dd\uac01\uc774 \uac00\ub054 \ub098"
                );

        boolean casinoFundingAmountInputIdentityVerificationErrorAttempt =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\ub3c4\ubc15"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\uc2dc \uce74\uc9c0\ub178\uc5d0 \ub4e4\uc5b4\uac14",
                        "\uce74\uc9c0\ub178\uc5d0 \ub4e4\uc5b4\uac14",
                        "\uce74\uc9c0\ub178\uc5d0 \ub4e4\uc5b4\uac00"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\ud588",
                        "\ub85c\uadf8\uc778\ud558\uace0",
                        "\ub85c\uadf8\uc778\ud588\uace0"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \uba54\ub274",
                        "\uc785\uae08\uba54\ub274"
                )
                && containsAny(
                        analysisText,
                        "\uae08\uc561\uae4c\uc9c0 \uc785\ub825\ud588",
                        "\uae08\uc561\uc744 \uc785\ub825\ud588",
                        "\uae08\uc561 \uc785\ub825\ud588"
                )
                && containsAny(
                        analysisText,
                        "\ubcf8\uc778\uc778\uc99d",
                        "\ubcf8\uc778 \uc778\uc99d"
                )
                && containsAny(
                        analysisText,
                        "\uc624\ub958\uac00 \ub098\uc11c",
                        "\uacc4\uc18d \uc624\ub958",
                        "\uc778\uc99d \uacfc\uc815\uc5d0\uc11c \uacc4\uc18d \uc624\ub958"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08\uc744 \ub05d\ub0b4\uc9c0 \ubabb",
                        "\uc785\uae08\uc744 \uc644\ub8cc\ud558\uc9c0 \ubabb",
                        "\uc785\uae08 \uc644\ub8cc\ub97c \ubabb"
                );

        boolean fundingAmountInputErrorRetryExternalFailure =
                containsAny(
                        analysisText,
                        "\ubca0\ud305\uc744 \ud558\ub824\uace0",
                        "\ubca0\ud305 \ud558\ub824\uace0",
                        "\ubca0\ud305\uc744 \ud574\ubcf4\ub824\uace0"
                )
                && containsAny(
                        analysisText,
                        "\uae08\uc561\uc744 \uc785\ub825\ud588",
                        "\uae08\uc561\uc744 \uc785\ub825\ud558\uace0",
                        "\uae08\uc561 \uc785\ub825\ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \ub2e8\uacc4\uc5d0\uc11c \uc624\ub958",
                        "\uc785\uae08 \ub2e8\uacc4\uc5d0 \uc624\ub958",
                        "\uc624\ub958\uac00 \ub098\uc11c"
                )
                && containsAny(
                        analysisText,
                        "\uba87 \ubc88 \ub2e4\uc2dc \ub20c\ub7ec",
                        "\ub2e4\uc2dc \ub20c\ub7ec",
                        "\ub2e4\uc2dc \ub20c\ub800"
                );

        boolean appStoreSearchExistenceConfirmedInstallNegatedSelfStop =
                containsAny(
                        analysisText,
                        "슬롯 앱 생각이 나서",
                        "슬롯 앱 생각이 났",
                        "슬롯 앱이 생각나서"
                )
                && containsAny(
                        analysisText,
                        "스토어에서 이름을 검색했습니다",
                        "스토어에서 이름을 검색했",
                        "스토어에서 앱 이름을 검색했"
                )
                && containsAny(
                        analysisText,
                        "앱이 아직 있는 건 확인",
                        "앱이 아직 있는 것을 확인",
                        "앱이 있는 건 확인"
                )
                && containsAny(
                        analysisText,
                        "설치 버튼은 누르지 않았",
                        "설치 버튼을 누르지 않았",
                        "설치는 하지 않았"
                );

        boolean slotGamblingUrgeRelatedAppSearchInstallNegatedSelfStop =
                containsAny(
                        analysisText,
                        "슬롯게임을 안 했",
                        "슬롯게임을"
                )
                && containsAny(
                        analysisText,
                        "다시 해볼까"
                )
                && containsAny(
                        analysisText,
                        "관련 앱을 찾아봤",
                        "관련 앱을 찾"
                )
                && containsAny(
                        analysisText,
                        "설치까지는 하지 않았",
                        "설치하지 않았"
                )
                && containsAny(
                        analysisText,
                        "오래 붙잡고 있을 것 같",
                        "괜히 시작하면"
                );
        boolean thirdPartyCasinoTriggerSelfSearchAppInfoInstallNegated =
                containsAny(
                        analysisText,
                        "카지노 얘기를 해서",
                        "카지노 이야기를 해서"
                )
                && containsAny(
                        analysisText,
                        "그 이름을 검색해봤",
                        "그 이름을 검색했"
                )
                && containsAny(
                        analysisText,
                        "앱 정보 화면까지 봤",
                        "앱 정보 화면을 봤"
                )
                && containsAny(
                        analysisText,
                        "설치는 안 했",
                        "설치하지 않았"
                );
        boolean selfCompletedMultiWagerLateSpouseContext =
                containsAny(
                        analysisText,
                        "\uc2a4\ud3ec\uce20\ubca0\ud305\uc744 \ud588",
                        "\uc2a4\ud3ec\uce20 \ubca0\ud305\uc744 \ud588",
                        "\ubca0\ud305\uc744 \ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc801\uc911\ud574\uc11c \uc870\uae08 \uc218\uc775",
                        "\uc801\uc911\ud574\uc11c",
                        "\uc218\uc775\uc774 \ub0ac"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\uc74c \uacbd\uae30\uc5d0\ub3c4",
                        "\ub2e4\uc74c \uacbd\uae30\uc5d0"
                )
                && containsAny(
                        analysisText,
                        "\ub450 \ubc88\uc9f8 \ubca0\ud305\uae4c\uc9c0 \ub05d\ub0b4",
                        "\ub450 \ubc88\uc9f8 \ubca0\ud305\uae4c\uc9c0 \ub05d",
                        "\ub450\ubc88\uc9f8 \ubca0\ud305\uae4c\uc9c0 \ub05d"
                )
                && containsAny(
                        analysisText,
                        "\ub354 \ud574\ub3c4 \ub420 \uac83 \uac19",
                        "\ub354 \ud574\ub3c4 \ub420\uac83 \uac19"
                )
                && containsAny(
                        analysisText,
                        "\ubc30\uc6b0\uc790\uac00 \uc606\uc5d0 \uc640",
                        "\ubc30\uc6b0\uc790\uac00"
                );


        boolean multiWagerCompletedLossAdditionalUrgePostCompletionStop =
                containsAny(
                        analysisText,
                        "\ubca0\ud305\uc744 \uc5ec\ub7ec \uac74 \ud588",
                        "\uc2e4\uc81c\ub85c \ubca0\ud305\uc744 \uc5ec\ub7ec \uac74"
                )
                && containsAny(
                        analysisText,
                        "\uac01\uac01 \uae08\uc561\uc744 \uc785\ub825",
                        "\uae08\uc561\uc744 \uc785\ub825\ud574\uc11c \ucc28\ub840\ub85c \uc81c\ucd9c",
                        "\ucc28\ub840\ub85c \uc81c\ucd9c"
                )
                && containsAny(
                        analysisText,
                        "\ub9c8\uc9c0\ub9c9 \uacbd\uae30\uae4c\uc9c0 \ubca0\ud305\uc774 \uc644\ub8cc",
                        "\ub9c8\uc9c0\ub9c9 \uacbd\uae30\uae4c\uc9c0 \ubca0\ud305 \uc644\ub8cc"
                )
                && containsAny(
                        analysisText,
                        "\ub9c8\uc9c0\ub9c9 \uacbd\uae30\uc5d0\uc11c \uc190\uc2e4",
                        "\uc190\uc2e4\uc774 \ub0ac"
                )
                && containsAny(
                        analysisText,
                        "\ub354 \ub123\uc73c\uba74 \ub2e4\uc2dc \ubc8c \uc218 \uc788\uc744 \uac83 \uac19",
                        "\ub2e4\uc2dc \ubc8c \uc218 \uc788\uc744 \uac83 \uac19"
                );

        boolean lossRecoveryLoginFundingTransferSelfStop =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\ub3c4\ubc15"
                )
                && containsAny(
                        analysisText,
                        "\ub9cc\ud68c\ud558\uace0 \uc2f6\uc5b4",
                        "\ub9cc\ud68c\ud558\uace0 \uc2f6"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d\ud574\uc11c \ub85c\uadf8\uc778",
                        "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08\ud560 \uae08\uc561\ub3c4 \uc815\ud574",
                        "\uc785\uae08\ud560 \uae08\uc561",
                        "\uae08\uc561\ub3c4 \uc815\ud574"
                )
                && containsAny(
                        analysisText,
                        "\uc740\ud589 \uc571\uc744 \uc5f4\uc5b4",
                        "\uc740\ud589\uc571\uc744 \uc5f4\uc5b4"
                )
                && containsAny(
                        analysisText,
                        "\uc774\uccb4\ud558\ub824\ub294 \uc21c\uac04",
                        "\uc774\uccb4\ud558\ub824\uace0"
                )
                && containsAny(
                        analysisText,
                        "\uc794\uc561\uc744 \ubcf4\ub2c8",
                        "\uc794\uc561\uc744 \ubcf4\uace0"
                )
                && containsAny(
                        analysisText,
                        "\ud654\uba74\uc744 \ub2eb",
                        "\ud654\uba74\uc744 \ub2eb\uc558"
                );

        boolean loginSuccessFundingAmountBatteryInterruptedAttempt =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\ub3c4\ubc15"
                )
                && containsAny(
                        analysisText,
                        "\uc608\uc804\uc5d0 \uc0ac\uc6a9\ud558\ub358 \uacc4\uc815",
                        "\uc608\uc804 \uacc4\uc815"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\uc5d0 \uc131\uacf5",
                        "\ub85c\uadf8\uc778\uc744 \uc131\uacf5"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \uba54\ub274\uae4c\uc9c0 \ub4e4\uc5b4\uac00",
                        "\uc785\uae08 \uba54\ub274"
                )
                && containsAny(
                        analysisText,
                        "\uae08\uc561\uc744 \uc801\uc5b4",
                        "\uae08\uc561\uc744 \uc785\ub825"
                )
                && containsAny(
                        analysisText,
                        "\ud734\ub300\ud3f0 \ubc30\ud130\ub9ac\uac00 \uac70\uc758 \ub2e4 \ub5a8\uc5b4",
                        "\ubc30\ud130\ub9ac\uac00 \uac70\uc758 \ub2e4 \ub5a8\uc5b4",
                        "\ubc30\ud130\ub9ac \ubd80\uc871"
                );

        boolean completedSlotAdditionalFundingLossNextDayAppDelete =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\uc2ac\ub86f"
                )
                && containsAny(
                        analysisText,
                        "\uc2ac\ub86f\uc744 \uba87 \ucc28\ub840 \ub3cc\ub838",
                        "\uc2ac\ub86f\uc744 \ub3cc\ub838"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\uc2dc \uc785\uae08\uc744 \ud558\uace0",
                        "\uc785\uae08\uc744 \ud558\uace0 \uacc4\uc18d"
                )
                && containsAny(
                        analysisText,
                        "\uacc4\uc18d \ub3cc\ub838",
                        "\uacc4\uc18d \ud588"
                )
                && containsAny(
                        analysisText,
                        "\ud6e8\uc52c \ub9ce\uc774 \uc783\uc5c8",
                        "\ub9ce\uc774 \uc783\uc5c8",
                        "\uc190\uc2e4"
                )
                && containsAny(
                        analysisText,
                        "\ub2e4\uc74c\ub0a0",
                        "\ub2e4\uc74c \ub0a0"
                )
                && containsAny(
                        analysisText,
                        "\uc571\uc744 \uc0ad\uc81c",
                        "\uc571\uc744 \uc9c0\uc6e0",
                        "\uc571\uc744 \uc9c0\uc6e0\uc2b5\ub2c8\ub2e4"
                );

        int urgeLogDelta =
                (
                        containsAffirmedUrge(analysisText)
                        || lossRecoveryLoginFundingTransferSelfStop
                        || thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge
                        || currentOccasionalGamblingThought
                        || selfUrgeAfterThirdPartyTrigger
                        || selfLossThoughtAfterThirdPartyTrigger
                        || selfUrgeSearchAfterThirdPartyTrigger
                        || selfSearchInputAfterThirdPartyTrigger
                        || selfSiteSearchAfterThirdPartyTrigger
                        || selfAppSearchAfterThirdPartyTrigger
                        || searchThoughtWithoutAttempt
                        || partialSearchInputThenSelfStopped
                        || selfCompletedMultiWagerLateSpouseContext
                        || multiWagerCompletedLossAdditionalUrgePostCompletionStop
                        || sportsMultiWagerNextDayUrgeMondayAppDeleted
                        || bettingIntentPartialSearchInputExternalDistraction
                        || relatedAppViewedThenDeleted
                        || relatedAppPresenceSearchThenSelfStopped
                        || relatedInterfaceReachedThenSelfStopped
                        || fundingScreenReachedThenSelfStopped
                        || wagerAmountInputDeletedThenDeviceHandoffRecovery
                        || fundingAmountInputErrorRetryExternalFailure
                        || appStoreSearchExistenceConfirmedInstallNegatedSelfStop
                )
                        ? 1
                        : 0;
        boolean wagerAmountInputThenWagerCompleted =
                containsAny(
                        analysisText,
                        "\uae08\uc561\uc744 \uc785\ub825\ud558\uace0",
                        "\ubca0\ud305 \uae08\uc561\uc744 \uc785\ub825\ud558\uace0",
                        "\uae08\uc561 \uc785\ub825\ud558\uace0"
                )
                && containsAny(
                        analysisText,
                        "\ubca0\ud305\uae4c\uc9c0 \ud588",
                        "\ubca0\ud305\uae4c\uc9c0 \ud558\uace0",
                        "\ubca0\ud305\uae4c\uc9c0 \uc644\ub8cc",
                        "\ubca0\ud305\uc744 \uc644\ub8cc"
                );

        boolean wagerCompletedThenFamilyDisclosureDeviceHandoffRecovery =
                containsAny(
                        analysisText,
                        "실제로 베팅을 하고",
                        "실제로 베팅하고",
                        "베팅을 하고 나서"
                )
                && containsAny(
                        analysisText,
                        "아내한테 얘기했습니다",
                        "아내한테 얘기했",
                        "아내에게 얘기했"
                )
                && containsAny(
                        analysisText,
                        "휴대폰을 잠깐 맡아달라고",
                        "휴대폰을 맡아달라고"
                )
                && containsAny(
                        analysisText,
                        "오늘은 제가 가지고 있지 않아요",
                        "오늘은 제가 가지고 있지 않",
                        "지금은 제가 가지고 있지 않"
                );
        boolean sportsResultOddsViewLateBettingContextThenSelfStopped =
                containsAny(
                        analysisText,
                        "스포츠 경기 결과",
                        "경기 결과"
                )
                && containsAny(
                        analysisText,
                        "배당도 같이 확인",
                        "배당을 같이 확인",
                        "배당도 확인"
                )
                && containsAny(
                        analysisText,
                        "예전 같으면 바로 베팅했",
                        "예전 같았으면 바로 베팅했"
                )
                && containsAny(
                        analysisText,
                        "경기 정보만 보고 껐",
                        "그냥 경기 정보만 보고 껐"
                );
        boolean oddsSearchAccessNaturalNoFurtherActionAttempt =
                containsAny(
                        analysisText,
                        "베팅을 하지는 않는데",
                        "베팅은 하지 않는데"
                )
                && containsAny(
                        analysisText,
                        "배당을 확인",
                        "배당을 확인하게"
                )
                && containsAny(
                        analysisText,
                        "검색해서 들어가 봤",
                        "검색해서 들어갔"
                )
                && containsAny(
                        analysisText,
                        "몇 경기만 확인",
                        "몇 경기 확인"
                )
                && containsAny(
                        analysisText,
                        "다른 일을 했",
                        "다른 일을 하"
                );
        boolean oddsSearchResultViewThenSelfStopped =
                containsAny(
                        analysisText,
                        "\ubc30\ub2f9\uc774 \uc5b4\ub5bb\uac8c \ubd99\ub294",
                        "\ubc30\ub2f9\uc744 \ucc3e\uc544\ubd24",
                        "\ubc30\ub2f9\uc744 \ucc3e\uc544\ubcf4",
                        "\ubc30\ub2f9\ud45c"
                )
                && containsAny(
                        analysisText,
                        "\uacbd\uae30 \uacb0\uacfc\ub9cc \ubcf4",
                        "\uacbd\uae30 \uacb0\uacfc"
                )
                && containsAny(
                        analysisText,
                        "\uadf8\ub0e5 \ub2eb",
                        "\ud654\uba74\uc744 \ub2eb",
                        "\ube0c\ub77c\uc6b0\uc800\ub97c \ub2eb"
                );

        boolean sportsResultOddsSelfClickAttempt =
                containsAny(
                        analysisText,
                        "경기 결과"
                )
                && containsAny(
                        analysisText,
                        "배당"
                )
                && containsAny(
                        analysisText,
                        "눌러봤",
                        "눌렀"
                );

        boolean thirdPartyLinkSelfOddsViewAttempt =
                containsAny(
                        analysisText,
                        "동료가 보내준 링크",
                        "동료가 보낸 링크",
                        "친구가 보내준 링크",
                        "친구가 보낸 링크"
                )
                && containsAny(
                        analysisText,
                        "눌러봤",
                        "눌렀"
                )
                && containsAny(
                        analysisText,
                        "배당만 잠깐 확인",
                        "배당을 잠깐 확인",
                        "배당만 확인"
                )
                && containsAny(
                        analysisText,
                        "베팅은 안 했",
                        "베팅하지 않았"
                );

        boolean thirdPartySportsTriggerSelfRelatedSiteSearchViewAttempt =
                containsAny(
                        analysisText,
                        "\uc2a4\ud3ec\uce20\ubca0\ud305\uc73c\ub85c \ub3c8\uc744 \ub530",
                        "\uc2a4\ud3ec\uce20\ubca0\ud305"
                )
                && containsAny(
                        analysisText,
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \ud558\ub098 \uac80\uc0c9",
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                )
                && containsAny(
                        analysisText,
                        "\ud654\uba74\uc744 \uc870\uae08 \uc77d",
                        "\ud654\uba74\uc744 \uc77d"
                )
                && containsAny(
                        analysisText,
                        "\ubcf5\uc7a1\ud574\uc11c \ub354 \uc54c\uc544\ubcf4\uc9c0",
                        "\ub354 \uc54c\uc544\ubcf4\uc9c0 \uc54a"
                );

        boolean gameSiteSearchScheduleOddsViewAttempt =
                containsAny(
                        analysisText,
                        "\uc608\uc804\uc5d0 \ubcf4\ub358 \uacbd\uae30 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9",
                        "\uacbd\uae30 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9",
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9"
                )
                && containsAny(
                        analysisText,
                        "\uacbd\uae30 \uc77c\uc815",
                        "\ud55c \uacbd\uae30 \uc815\ub3c4 \ub20c\ub7ec",
                        "\ubc30\ub2f9 \uc22b\uc790"
                )
                && containsAny(
                        analysisText,
                        "\ubc30\ub2f9 \uc22b\uc790",
                        "\ubc30\ub2f9"
                );

        boolean thirdPartySportsTriggerSelfSiteSearchOddsViewAttempt =
                selfContextExtracted
                && thirdPartyGamblingContextForSelfUrge
                && containsAny(
                        analysisText,
                        "\uc5b4\ub5a4 \uc0ac\uc774\ud2b8\uc778\uc9c0 \ucc3e\uc544\ubd24",
                        "\uc0ac\uc774\ud2b8\uc778\uc9c0 \ucc3e\uc544\ubd24"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9 \uacb0\uacfc\uc5d0 \ub098\uc628 \uc774\ub984",
                        "\uac80\uc0c9 \uacb0\uacfc"
                )
                && containsAny(
                        analysisText,
                        "\ubc30\ub2f9 \ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\ub3c8\uc744 \ub123\uac70\ub098 \ubca0\ud305\ud55c \uac74 \uc5c6",
                        "\ub3c8\uc744 \ub123\uac70\ub098 \ubca0\ud305\ud55c \uac83\uc740 \uc5c6"
                );
        boolean loginCompletedWagerScreenSelfExitNextDayAppDeleteRecovery =
                containsAny(
                        analysisText,
                        "\uc9c0\ub09c\ubc24\uc5d0",
                        "\uc5b4\uc82f\ubc24\uc5d0"
                )
                && containsAny(
                        analysisText,
                        "\uc608\uc804 \uacc4\uc815\uc73c\ub85c \ub85c\uadf8\uc778\uc744 \ud588",
                        "\ub85c\uadf8\uc778\uc740 \ub410\ub294\ub370",
                        "\ub85c\uadf8\uc778\uc740 \ub410"
                )
                && containsAny(
                        analysisText,
                        "\ubca0\ud305 \ud654\uba74\uc5d0\uc11c \uadf8\ub0e5 \ub098\uc654",
                        "\ubca0\ud305 \ud654\uba74\uc5d0\uc11c \ub098\uc654"
                )
                && containsAny(
                        analysisText,
                        "\uc624\ub298 \uc544\uce68\uc5d0 \uc571\ub3c4 \uc9c0\uc6e0",
                        "\uc624\ub298 \uc544\uce68\uc5d0 \uc571\uc744 \uc9c0\uc6e0"
                );
        boolean slotResultSearchThenNextDayAppDeletedRecovery =
                containsAny(
                        analysisText,
                        "어제는 슬롯 결과를 좀 찾아봤",
                        "어제 슬롯 결과를 좀 찾아봤",
                        "슬롯 결과를 찾아봤",
                        "슬롯 결과를 좀 찾아봤"
                )
                && containsAny(
                        analysisText,
                        "오늘 아침에는 앱을 삭제했",
                        "오늘 아침에 앱을 삭제했",
                        "오늘 아침에는 앱을 지웠",
                        "오늘 아침에 앱을 지웠"
                );
        boolean pastGamblingAdKnownGameExistenceSearchAttempt =
                priorGamblingContextForCurrentThought
                && containsAny(
                        analysisText,
                        "\uad00\ub828 \uad11\uace0",
                        "\uad11\uace0\uac00 \ub5a0"
                )
                && containsAny(
                        analysisText,
                        "\uc608\uc804\uc5d0 \uc54c\ub358 \uac8c\uc784",
                        "\uc54c\ub358 \uac8c\uc784"
                )
                && containsAny(
                        analysisText,
                        "\uc544\uc9c1 \uc788\ub294\uc9c0",
                        "\uc544\uc9c1 \uc788\ub294\uc9c0\ub3c4"
                )
                && containsAny(
                        analysisText,
                        "\ucc3e\uc544\ubd24",
                        "\ucc3e\uc544\ubcf4"
                );

        boolean casinoAppInstallLoginFailurePhoneInterruptedAttempt =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178 \uc571",
                        "\ub3c4\ubc15 \uc571"
                )
                && containsAny(
                        analysisText,
                        "\uc571\uc744 \uac80\uc0c9\ud574\uc11c \uc124\uce58",
                        "\uc571\uc744 \uac80\uc0c9\ud574 \uc124\uce58",
                        "\uc124\uce58\uae4c\uc9c0 \ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc608\uc804\uc5d0 \uc4f0\ub358 \ubc88\ud638\ub97c \ub123",
                        "\ube44\ubc00\ubc88\ud638\ub97c \ub123",
                        "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\uc5d0 \uc2e4\ud328",
                        "\ub85c\uadf8\uc778\uc774 \uc2e4\ud328",
                        "\ub85c\uadf8\uc778 \uc2e4\ud328"
                )
                && containsAny(
                        analysisText,
                        "\ube44\ubc00\ubc88\ud638 \ucc3e\uae30",
                        "\ube44\ubc00\ubc88\ud638\ucc3e\uae30"
                )
                && containsAny(
                        analysisText,
                        "\uc804\ud654\uac00 \uc640\uc11c",
                        "\uc804\ud654\uac00 \uc654"
                );

        boolean loginClickPasswordFailureAttempt =
                (
                        containsAny(
                                analysisText,
                                "\ubca0\ud305 \uc0ac\uc774\ud2b8",
                                "\ub3c4\ubc15 \uc0ac\uc774\ud2b8",
                                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8"
                        )
                        && containsAny(
                                analysisText,
                                "\ub85c\uadf8\uc778 \ucc3d",
                                "\ub85c\uadf8\uc778 \ud654\uba74"
                        )
                        && containsAny(
                                analysisText,
                                "\ub85c\uadf8\uc778\uc744 \ub20c\ub800",
                                "\ub85c\uadf8\uc778 \ubc84\ud2bc\uc744 \ub20c\ub800"
                        )
                        && containsAny(
                                analysisText,
                                "\ube44\ubc00\ubc88\ud638\uac00 \ub9de\uc9c0 \uc54a",
                                "\ube44\ubc00\ubc88\ud638\uac00 \ud2c0\ub838",
                                "\ube44\ubc00\ubc88\ud638 \uc624\ub958"
                        )
                )
                || (
                        containsAny(
                                analysisText,
                                "\uc2a4\ud3ec\uce20\ubca0\ud305 \uacc4\uc815",
                                "\uc2a4\ud3ec\uce20 \ubca0\ud305 \uacc4\uc815"
                        )
                        && containsAny(
                                analysisText,
                                "\uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d",
                                "\uc0ac\uc774\ud2b8\ub97c \uc811\uc18d"
                        )
                        && containsAny(
                                analysisText,
                                "\uc544\uc774\ub514\ub97c \ub123\uace0",
                                "\uc544\uc774\ub514\ub97c \uc785\ub825"
                        )
                        && containsAny(
                                analysisText,
                                "\ube44\ubc00\ubc88\ud638\ub3c4 \uc785\ub825",
                                "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825"
                        )
                        && containsAny(
                                analysisText,
                                "\ub85c\uadf8\uc778\uc774 \ud55c \ubc88 \uc2e4\ud328",
                                "\ub85c\uadf8\uc778\uc774 \uc2e4\ud328",
                                "\ub85c\uadf8\uc778 \uc2e4\ud328"
                        )
                );

        boolean casinoSiteAccessLoginButtonClickAttempt =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8 \uc8fc\uc18c\ub97c \ucc3e\uc544",
                        "\uac80\uc0c9\ud574\uc11c \uc0ac\uc774\ud2b8",
                        "\uc811\uc18d\ub3c4 \ud588"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778 \ubc84\ud2bc\uae4c\uc9c0 \ub20c\ub800",
                        "\ub85c\uadf8\uc778 \ubc84\ud2bc\uc744 \ub20c\ub800"
                );

        boolean pastGamblingAccountLoginOnlyAttempt =
                containsGeneralReentryLoginCompleted(analysisText)
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\ub9cc \ud574\ubd24\uc5b4\uc694",
                        "\ub85c\uadf8\uc778\ub9cc \ud574\ubd24"
                );

        boolean bettingUrgeSiteLoginFundingPhoneInterrupted =
                containsAny(
                        analysisText,
                        "\ubca0\ud305",
                        "\ubc30\ud305",
                        "\uc2a4\ud3ec\uce20\ubca0\ud305",
                        "\uc2a4\ud3ec\uce20\ubc30\ud305"
                )
                && containsAny(
                        analysisText,
                        "\ub610 \ud574\ubcf4\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                        "\ud574\ubcf4\uace0 \uc2f6\uc740 \ub9c8\uc74c"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud574\uc11c \uc811\uc18d",
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud574\uc11c"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778\uae4c\uc9c0\ub294 \ud588",
                        "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588"
                )
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \ud654\uba74\uc744 \uc5f4\uc5b4",
                        "\uc785\uae08 \ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\uae08\uc561\uc744 \uc801\uc73c\ub824\ub358",
                        "\uae08\uc561\uc744 \uc785\ub825\ud558\ub824"
                )
                && containsAny(
                        analysisText,
                        "\ud68c\uc0ac\uc5d0\uc11c \uc804\ud654\uac00 \uc640",
                        "\uc804\ud654\uac00 \uc640"
                );

        boolean loginCompletedFundingScreenPhoneInterruptedAttempt =
                containsGeneralReentryLoginCompleted(analysisText)
                && containsAny(
                        analysisText,
                        "\uc785\uae08 \ud654\uba74\uc744 \uc5f4\uc5b4",
                        "\uc785\uae08 \ud654\uba74\uc744 \uc5f4",
                        "\uc785\uae08 \ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\uae08\uc561\uc744 \uc801\uc73c\ub824\ub358",
                        "\uae08\uc561\uc744 \uc801\uc73c\ub824",
                        "\uae08\uc561\uc744 \uc785\ub825\ud558\ub824"
                )
                && containsAny(
                        analysisText,
                        "\ud68c\uc0ac\uc5d0\uc11c \uc804\ud654\uac00 \uc640",
                        "\uc804\ud654\uac00 \uc640"
                );
        boolean wagerCompletedThenNextDayFamilyDeviceHandoff =
                containsAny(
                        analysisText,
                        "실제로 베팅을 한 뒤",
                        "실제로 베팅한 뒤"
                )
                && containsAny(
                        analysisText,
                        "다음 날 휴대폰을 가족한테 맡겼",
                        "다음 날 휴대폰을 가족에게 맡겼"
                );
        boolean casinoAddressSelfClickConnectionFailureAttempt =
                (
                        containsAny(
                                analysisText,
                                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8 \uc8fc\uc18c\ub97c \ubc1b\uc544\uc11c",
                                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8 \uc8fc\uc18c\ub97c \ubc1b"
                        )
                        && containsAny(
                                analysisText,
                                "\ub20c\ub7ec\ubcf4\uae34 \ud588",
                                "\ub20c\ub7ec\ubd24",
                                "\ub20c\ub7ec"
                        )
                        && containsAny(
                                analysisText,
                                "\uc811\uc18d\uc774 \ub04a\uaca8",
                                "\uc811\uc18d\uc774 \ub04a\uacbc",
                                "\ud654\uba74\uc744 \uc81c\ub300\ub85c \ubabb \ubd24",
                                "\uc811\uc18d\uc774 \uc548 \ub410",
                                "\uc811\uc18d\uc774 \uc548\ub410",
                                "\ud654\uba74\uc774 \uc815\uc0c1\uc801\uc73c\ub85c \ubabb \ub5b4"
                        )
                )
                || (
                        containsAny(
                                analysisText,
                                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uc785\ub825",
                                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8 \uc774\ub984"
                        )
                        && containsAny(
                                analysisText,
                                "\uac80\uc0c9 \uacb0\uacfc\ub97c \ud655\uc778",
                                "\uac80\uc0c9 \uacb0\uacfc",
                                "\uac80\uc0c9\uacb0\uacfc"
                        )
                        && containsAny(
                                analysisText,
                                "\ub9c1\ud06c \ud558\ub098\ub97c \ub20c\ub800",
                                "\ub9c1\ud06c\ub97c \ub20c\ub800",
                                "\ub9c1\ud06c \ud558\ub098\ub97c \ub20c"
                        )
                        && containsAny(
                                analysisText,
                                "\uc811\uc18d \uc624\ub958",
                                "\ud654\uba74\uc774 \uc5f4\ub9ac\uc9c0 \uc54a",
                                "\ud654\uba74\uc774 \uc548 \uc5f4"
                        )
                );
        boolean wagerScreenAmountDecisionSubmitNegatedAttempt =
                containsAny(
                        analysisText,
                        "베팅 화면까지 갔",
                        "베팅 화면까지 들어갔"
                )
                && containsAny(
                        analysisText,
                        "금액을 정하는 데 시간이",
                        "금액을 정하는데 시간이",
                        "금액을 정하"
                )
                && containsAny(
                        analysisText,
                        "버튼은 안 눌렀",
                        "버튼을 안 눌렀",
                        "버튼은 누르지 않았",
                        "버튼을 누르지 않았"
                );
        boolean pastCasinoAppExecutedScreenViewedAttempt =
                containsAny(
                        analysisText,
                        "예전에 쓰던 카지노 앱",
                        "예전 카지노 앱"
                )
                && containsAny(
                        analysisText,
                        "실행해서",
                        "실행했"
                )
                && containsAny(
                        analysisText,
                        "예전 화면을 봤",
                        "화면을 봤"
                );
        boolean bettingAppExecutedRecordViewedThenSelfStopped =
                containsAny(
                        analysisText,
                        "\ubca0\ud305 \uc571",
                        "\uc2a4\ud3ec\uce20\ubca0\ud305 \uc571"
                )
                && containsAny(
                        analysisText,
                        "\uc571\uc744 \uc5f4\uc5b4\ubd24",
                        "\uc571\uc744 \uc5f4\uc5b4\ubcf4"
                )
                && containsAny(
                        analysisText,
                        "\uc608\uc804 \uae30\ub85d",
                        "\uae30\ub85d \uac19\uc740 \uac8c \ub0a8\uc544"
                )
                && containsAny(
                        analysisText,
                        "\ud654\uba74\uc744 \ub044",
                        "\ud734\ub300\ud3f0\uc744 \ucc45\uc0c1\uc5d0 \ub194"
                );

        boolean sportsBettingAppReinstallLoginAttempt =
                containsAny(
                        analysisText,
                        "\ubc30\ub2f9 \uc22b\uc790",
                        "\ubca0\ud305",
                        "\uc2a4\ud3ec\uce20\ubca0\ud305"
                )
                && containsAny(
                        analysisText,
                        "\uad00\ub828 \uc571\uc744 \ub2e4\uc2dc \uc124\uce58",
                        "\uc571\uc744 \ub2e4\uc2dc \uc124\uce58"
                )
                && containsAny(
                        analysisText,
                        "\uc2e4\ud589\uae4c\uc9c0 \ud588",
                        "\uc2e4\ud589"
                )
                && containsAny(
                        analysisText,
                        "\uacb0\uad6d \ub85c\uadf8\uc778",
                        "\ub85c\uadf8\uc778\ud588\uc2b5\ub2c8\ub2e4",
                        "\ub85c\uadf8\uc778\ud588"
                );

        boolean sportsBettingLoginFundingAuthErrorPhoneInterrupted =
                containsAny(
                        analysisText,
                        "\uc2a4\ud3ec\uce20\ubca0\ud305",
                        "\uc2a4\ud3ec\uce20 \ubca0\ud305"
                )
                && containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544 \ub85c\uadf8\uc778",
                        "\uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544\uc11c \ub85c\uadf8\uc778",
                        "\uacb0\uad6d \uc0ac\uc774\ud2b8\ub97c \ucc3e\uc544 \ub85c\uadf8\uc778"
                )
                && containsAny(
                        analysisText,
                        "\ucd94\uac00 \uc785\uae08\uc744 \uc2dc\ub3c4",
                        "\ucd94\uac00\uc785\uae08\uc744 \uc2dc\ub3c4",
                        "\uc785\uae08\uc744 \uc2dc\ub3c4"
                )
                && containsAny(
                        analysisText,
                        "\ubcf8\uc778\uc778\uc99d \ub2e8\uacc4\uc5d0\uc11c \uc624\ub958",
                        "\ubcf8\uc778\uc778\uc99d \ub2e8\uacc4\uc5d0 \uc624\ub958",
                        "\ubcf8\uc778\uc778\uc99d",
                        "\uc778\uc99d \ub2e8\uacc4\uc5d0\uc11c \uc624\ub958"
                )
                && containsAny(
                        analysisText,
                        "\uc804\ud654\uac00 \uc640\uc11c \uc911\ub2e8",
                        "\uc804\ud654\uac00 \uc640\uc11c",
                        "\uc804\ud654\uac00 \uc654"
                );

        boolean sportsBettingAppLoginPhoneInterrupted =
                sportsBettingAppReinstallLoginAttempt
                && containsAny(
                        analysisText,
                        "\uce5c\uad6c\ud55c\ud14c \uc804\ud654\uac00 \uc654",
                        "\uce5c\uad6c\uc5d0\uac8c \uc804\ud654\uac00 \uc654",
                        "\uc804\ud654\uac00 \uc654"
                )
                && containsAny(
                        analysisText,
                        "\ud1b5\ud654\ud558\uba74\uc11c",
                        "\ud1b5\ud654\ud558\ub2e4",
                        "\ud1b5\ud654"
                );

        boolean sportsBettingAppLaterDeletedRecovery =
                sportsBettingAppReinstallLoginAttempt
                && containsAny(
                        analysisText,
                        "\uc571\uc744 \uc9c0\uc6b8\uc9c0 \ub9d0\uc9c0",
                        "\uc571\uc744 \uc9c0\uc6b8"
                )
                && containsAny(
                        analysisText,
                        "\uc8fc\ub9d0\uc5d0 \uadf8\ub0e5 \uc0ad\uc81c",
                        "\uadf8\ub0e5 \uc0ad\uc81c\ud588",
                        "\uc571\uc744 \uc0ad\uc81c\ud588"
                );


        int betAttemptDelta =
                (
                        containsAffirmedAttempt(analysisText)
                        || lossRecoveryLoginFundingTransferSelfStop
                        || thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge
                        || sportsBettingLoginFundingAuthErrorPhoneInterrupted
                        || sportsBettingAppReinstallLoginAttempt
                        || bettingAppExecutedRecordViewedThenSelfStopped
                        || pastCasinoAppExecutedScreenViewedAttempt
                        || wagerScreenAmountDecisionSubmitNegatedAttempt
                        || casinoAddressSelfClickConnectionFailureAttempt
                        || wagerCompletedThenNextDayFamilyDeviceHandoff
                        || pastGamblingAccountLoginOnlyAttempt
                        || casinoSiteAccessLoginButtonClickAttempt
                        || bettingUrgeSiteLoginFundingPhoneInterrupted
                        || loginCompletedFundingScreenPhoneInterruptedAttempt
                        || loginSuccessFundingAmountBatteryInterruptedAttempt
                        || casinoAppInstallLoginFailurePhoneInterruptedAttempt
                        || loginClickPasswordFailureAttempt
                        || pastGamblingAdKnownGameExistenceSearchAttempt
                        || slotResultSearchThenNextDayAppDeletedRecovery
                        || selfLossThoughtAfterThirdPartyTrigger
                        || selfUrgeSearchAfterThirdPartyTrigger
                        || selfSiteSearchAfterThirdPartyTrigger
                        || selfAppSearchAfterThirdPartyTrigger
                        || bettingIntentPartialSearchInputExternalDistraction
                        || partialSearchInputThenSelfStopped
                        || thirdPartyTriggerSelfPartialSearchInputThenSelfStopped
                        || thirdPartyLinkCasinoAccessThenSelfStopped
                        || lateDomainCasinoAccessThenSelfStopped
                        || siteNameInputCompleteThenOtherTaskSelfStopped
                        || relatedAppViewedThenDeleted
                        || relatedAppPresenceSearchThenSelfStopped
                        || gamblingSiteResultViewThenSelfExited
                        || wagerCompletedThenNextDayAppDeletedRecovery
                        || completedSlotAdditionalFundingLossNextDayAppDelete
                        || sportsMultiWagerNextDayUrgeMondayAppDeleted
                        || sportsBettingSearchAutocompleteThenSelfStopped
                        || pastSportsBettingSiteSearchResultViewedThenSelfStopped
                        || bettingAmountViewedThenSelfStopped
                        || slotAppReinstallLoginFundingAmountDeleteSelfStop
                        || appReinstallLoginFundingScreenThenSelfStopped
                        || wagerScreenReachedBeforeAmountInputThenSelfExited
                        || wagerScreenReachedMotivationDropThenSelfExited
                        || wagerAmountInputDeletedThenDeviceHandoffRecovery
                        || bettingAppAmountInputPhoneInterruptedAttempt
                        || sportsBettingAmountInputSiblingInterruptedAttempt
                        || wagerAmountInputPhoneInterruptedNextDaySelfExited
                        || wagerAmountInputDeletedThenSelfExited
                        || bettingAppAmountInputUncertainSubmitSelfCancel
                        || selfCompletedMultiWagerLateSpouseContext
                        || multiWagerCompletedLossAdditionalUrgePostCompletionStop
                        || wagerCompletedRelapseThenNextDayNoAction
                        || appReinstalledAndExecutedWithUrgeNegation
                        || existingSlotAppLoggedInGameScreenThenSelfStopped
                        || relatedInterfaceReachedThenSelfStopped
                        || fundingScreenReachedThenSelfStopped
                        || thirdPartyTriggerSelfBettingSiteAccessFundingScreenNoAmountInput
                        || wagerAmountInputThenWagerCompleted
                        || casinoFundingAmountInputIdentityVerificationErrorAttempt
                        || fundingAmountInputErrorRetryExternalFailure
                        || thirdPartyHistoryExplicitSelfSiteSearch
                        || thirdPartyHistorySelfSiteSearchAttempt
                        || wagerCompletedThenFamilyDisclosureDeviceHandoffRecovery
                        || appStoreSearchExistenceConfirmedInstallNegatedSelfStop
                        || slotGamblingUrgeRelatedAppSearchInstallNegatedSelfStop
                        || thirdPartyCasinoTriggerSelfSearchAppInfoInstallNegated
                        || thirdPartySlotTriggerSelfAppInstallExecuteSelfStopNextDayDelete
                        || selfSiteAccessLoginIdInputPasswordNotEnteredThenSelfStopped
                        || thirdPartyTriggerSelfSiteAccessLoginIdInputThenSelfStopped
                        || casinoSiteNameSearchLoginScreenReachedAttempt
                        || thirdPartyCasinoTriggerSelfSearchLoginScreenThenSelfStopped
                        || thirdPartyCasinoTriggerSelfSlotAppSearchAutocompleteAttempt
                        || thirdPartyLinkSelfSiteAccessPreLoginSelfStop
                        || thirdPartyLinkSelfWagerInputDeleteSelfStop
                        || thirdPartyLinkSelfOddsViewAttempt
                        || sportsResultOddsSelfClickAttempt
                        || oddsSearchResultViewThenSelfStopped
                        || oddsSearchAccessNaturalNoFurtherActionAttempt
                        || sportsResultOddsViewLateBettingContextThenSelfStopped
                        || thirdPartySportsTriggerSelfSiteSearchOddsViewAttempt
                        || thirdPartySportsTriggerSelfRelatedSiteSearchViewAttempt
                        || thirdPartySportsOddsSelfPageAccessAttempt
                        || gameSiteSearchScheduleOddsViewAttempt
                        || loginCompletedWagerScreenSelfExitNextDayAppDeleteRecovery
                        || thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge
                )
                        ? 1
                        : 0;
        boolean searchBoxOpenedWithoutInputThenNaturalDrop =
                containsAny(
                        analysisText,
                        "\uc0ac\uc774\ud2b8 \uc774\ub984\uc774 \uac11\uc790\uae30 \uc0dd\uac01",
                        "\uc0ac\uc774\ud2b8 \uc774\ub984"
                )
                && containsAny(
                        analysisText,
                        "\uac80\uc0c9\ucc3d\uae4c\uc9c0 \uc5f4\uc5b4",
                        "\uac80\uc0c9\ucc3d\uc744 \uc5f4\uc5b4"
                )
                && !containsAny(
                        analysisText,
                        "\uba87 \uae00\uc790\ub97c \uc37c",
                        "\uc774\ub984\uc744 \uc785\ub825",
                        "\uac80\uc0c9\uc5b4\ub97c \uc785\ub825",
                        "\uac80\uc0c9\ud588"
                )
                && containsAny(
                        analysisText,
                        "\uadc0\ucc2e\uc544",
                        "\ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub193",
                        "\uc53b\uc5c8"
                );

        boolean casinoLoginPageCompanyPhoneExternalInterruption =
                containsAny(
                        analysisText,
                        "\uce74\uc9c0\ub178",
                        "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8"
                )
                && containsAny(
                        analysisText,
                        "\ub85c\uadf8\uc778 \ud398\uc774\uc9c0",
                        "\ub85c\uadf8\uc778 \ud654\uba74"
                )
                && containsAny(
                        analysisText,
                        "\ud68c\uc0ac\uc5d0\uc11c \uc804\ud654\uac00 \uc640",
                        "\ud68c\uc0ac \uc804\ud654\uac00 \uc640",
                        "\uc804\ud654\uac00 \uc640\uc11c"
                )
                && containsAny(
                        analysisText,
                        "\ud1b5\ud654\ub97c \ud558\ub290\ub77c",
                        "\ud1b5\ud654\ud558\ub290\ub77c",
                        "\ud1b5\ud654"
                );

        int betBlockedDelta =
                (
                        (
                                containsProtectiveBlock(analysisText)
                                && !completedWagerNextMorningAppDeletedRelapseRecovery
                                && !wagerCompletedThenNextDayAppDeletedRecovery
                                && !searchBoxOpenedWithoutInputThenNaturalDrop
                                && !selfCompletedMultiWagerLateSpouseContext
                                && !multiWagerCompletedLossAdditionalUrgePostCompletionStop
                        )
                        || lossRecoveryLoginFundingTransferSelfStop
                        || loginScreenReachedThenPhonePutDown
                        || thirdPartySportsOddsSelfPageAccessThenSelfStopped
                        || loginScreenReachedThenBrowserClosed
                        || loginScreenPasswordEntryConsideredThenSelfStopped
                        || bettingAppExecutedRecordViewedThenSelfStopped
                        || partialSearchInputThenSelfStopped
                        || thirdPartyTriggerSelfPartialSearchInputThenSelfStopped
                        || thirdPartyLinkCasinoAccessThenSelfStopped
                        || lateDomainCasinoAccessThenSelfStopped
                        || selfSiteAccessLoginIdInputPasswordNotEnteredThenSelfStopped
                        || thirdPartyTriggerSelfSiteAccessLoginIdInputThenSelfStopped
                        || thirdPartyCasinoTriggerSelfSearchLoginScreenThenSelfStopped
                        || siteNameInputCompleteThenOtherTaskSelfStopped
                        || relatedAppViewedThenDeleted
                        || relatedAppPresenceSearchThenSelfStopped
                        || gamblingSiteResultViewThenSelfExited
                        || sportsBettingSearchAutocompleteThenSelfStopped
                        || pastSportsBettingSiteSearchResultViewedThenSelfStopped
                        || sportsResultOddsViewLateBettingContextThenSelfStopped
                        || oddsSearchResultViewThenSelfStopped
                        || bettingAmountViewedThenSelfStopped
                        || slotAppReinstallLoginFundingAmountDeleteSelfStop
                        || appReinstallLoginFundingScreenThenSelfStopped
                        || wagerScreenReachedBeforeAmountInputThenSelfExited
                        || wagerScreenReachedMotivationDropThenSelfExited
                        || wagerAmountInputDeletedThenDeviceHandoffRecovery
                        || wagerAmountInputPhoneInterruptedNextDaySelfExited
                        || wagerAmountInputDeletedThenSelfExited
                        || thirdPartyLinkSelfWagerInputDeleteSelfStop
                        || bettingAppAmountInputUncertainSubmitSelfCancel
                        || existingSlotAppLoggedInGameScreenThenSelfStopped
                        || relatedInterfaceReachedThenSelfStopped
                        || fundingScreenReachedThenSelfStopped
                        || loginCompletedFundingScreenThenSelfExited
                        || appStoreSearchExistenceConfirmedInstallNegatedSelfStop
                        || slotGamblingUrgeRelatedAppSearchInstallNegatedSelfStop
                        || thirdPartyCasinoTriggerSelfSearchAppInfoInstallNegated
                        || thirdPartySlotTriggerSelfAppInstallExecuteSelfStopNextDayDelete
                        || thirdPartyLinkSelfSiteAccessPreLoginSelfStop
                        || loginCompletedWagerScreenSelfExitNextDayAppDeleteRecovery
                        || thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge
                )
                && !sportsBettingAppLoginPhoneInterrupted
                && !sportsBettingLoginFundingAuthErrorPhoneInterrupted
                && !casinoLoginPageCompanyPhoneExternalInterruption
                        ? 1
                        : 0;
        boolean paymentMethodFamilyHandoffRecovery =
                containsAny(
                        analysisText,
                        "\uacb0\uc81c\uc218\ub2e8\uc744 \uac00\uc871\ud55c\ud14c \ub9e1\uaca8",
                        "\uacb0\uc81c\uc218\ub2e8\uc744 \uac00\uc871\uc5d0\uac8c \ub9e1\uaca8",
                        "\uacb0\uc81c\uc218\ub2e8\uc744 \uac00\uc871\ud55c\ud14c \ub9e1\uaca8\ub450",
                        "\uacb0\uc81c\uc218\ub2e8\uc744 \uac00\uc871\uc5d0\uac8c \ub9e1\uaca8\ub450"
                );

        int recoveryActionDelta =
                (
                        containsRecoveryAction(analysisText)
                        || paymentMethodFamilyHandoffRecovery
                        || sportsMultiWagerNextDayUrgeMondayAppDeleted
                        || sportsBettingAppLaterDeletedRecovery
                        || thirdPartySlotTriggerSelfAppInstallExecuteSelfStopNextDayDelete
                        || wagerCompletedThenNextDayFamilyDeviceHandoff
                        || slotResultSearchThenNextDayAppDeletedRecovery
                        || completedWagerNextMorningAppDeletedRelapseRecovery
                        || wagerCompletedThenNextDayAppDeletedRecovery
                        || completedSlotAdditionalFundingLossNextDayAppDelete
                        || wagerAmountInputDeletedThenDeviceHandoffRecovery
                        || wagerCompletedThenFamilyDisclosureDeviceHandoffRecovery
                        || loginCompletedWagerScreenSelfExitNextDayAppDeleteRecovery
                )
                        ? 1
                        : 0;
        int relapseSignalDelta =
                (
                        containsRelapseSignal(analysisText)
                        || thirdPartyTriggerSelfCasinoFundingSlotSelfStopCurrentUrge
                        || sportsMultiWagerNextDayUrgeMondayAppDeleted
                        || wagerCompletedThenNextDayFamilyDeviceHandoff
                        || completedWagerNextMorningAppDeletedRelapseRecovery
                        || wagerCompletedThenNextDayAppDeletedRecovery
                        || completedSlotAdditionalFundingLossNextDayAppDelete
                        || completedFundingSingleWagerPostNoActionRelapse
                        || selfCompletedMultiWagerLateSpouseContext
                        || multiWagerCompletedLossAdditionalUrgePostCompletionStop
                        || wagerCompletedRelapseThenNextDayNoAction
                        || wagerCompletedThenFamilyDisclosureDeviceHandoffRecovery
                )
                        ? 1
                        : 0;

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
                "배우자가",
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
                "저는",
                "저도"
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
                "오래전에는",
                "몇 달 전에는"
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
                "최근에",
                "최근에는",
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
                "하고 싶은 생각은 전혀 들지 않",
                "별로 하고 싶은 마음은 안 들어"
        )) {
            return false;
        }


        if (
                containsAny(
                        text,
                        "\ubca0\ud305",
                        "\ubc30\ud305",
                        "\uc2a4\ud3ec\uce20\ubca0\ud305",
                        "\uc2a4\ud3ec\uce20\ubc30\ud305",
                        "\ub3c4\ubc15",
                        "\uce74\uc9c0\ub178",
                        "\uc2ac\ub86f"
                )
                && containsAny(
                        text,
                        "\ub610 \ud574\ubcf4\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                        "\ud574\ubcf4\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                        "\uc544\uc26c\uc6b4 \ub9c8\uc74c\uc774 \uc0dd\uae30",
                        "\uc870\uae08 \uc544\uc26c\uc6b4 \ub9c8\uc74c",
                        "\ub354 \ub123\uc744\uae4c \uace0\ubbfc",
                        "\ub354 \ub123\uc744\uae4c"
                )
        ) {
            return true;
        }

        boolean slotCurrentUrge =
                containsAny(
                        text,
                        "\uc2ac\ub86f",
                        "\uc2ac\ub86f \uc571"
                )
                && (
                        containsAny(
                                text,
                                "\uacc4\uc18d \uc0dd\uac01\uc774 \ub098",
                                "\uba70\uce60 \uc804\ubd80\ud130 \uacc4\uc18d \uc0dd\uac01",
                                "\uba70\uce60 \uc804\ubd80\ud130 \uc0dd\uac01"
                        )
                        || containsAny(
                                text,
                                "\uadf8\ub0e5 \ud574\ubcf4\uc790\ub294 \uc0dd\uac01",
                                "\uccab \ud310\uc740 \uadf8\ub0e5 \ud574\ubcf4\uc790",
                                "\ud574\ubcf4\uc790\ub294 \uc0dd\uac01"
                        )
                );

        if (slotCurrentUrge) {
            return true;
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
                        "\uc0c8\ub85c \uac00\uc785\ud560\uae4c",
                        "\ub2e4\uc2dc \uac00\uc785\ud560\uae4c"
                )
        ) {
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

        if (
                containsIndirectGamblingSiteSearchAttempt(text)
                && containsAny(
                        text,
                        "\ud558\uace0 \uc2f6\uc5b4\uc84c",
                        "\ub610 \ud574\ubcf4\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                        "\ud574\ubcf4\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                        "\ub610 \uc0dd\uac01\ub0ac",
                        "\uadf8\ub0a0 \ubc24 \ub610 \uc0dd\uac01\ub0ac",
                        "\uc608\uc804 \uc0dd\uac01\uc774 \uacc4\uc18d \ub0ac"
                )
        ) {
            return true;
        }

        if (
                containsLoginScreenEntryAttempt(text)
                && containsAny(
                        text,
                        "\ub2e4\uc2dc \ud574\ubcfc\uae4c \uc2f6\uc5c8",
                        "\ub2e4\uc2dc \ud574\ubcfc\uae4c \ud558\ub294 \uc0dd\uac01",
                        "\ube44\uc2b7\ud55c \uc0dd\uac01\uc774 \ub0ac"
                )
        ) {
            return true;
        }

        if (containsAny(
                text,
                "\ub3c4\ubc15 \uc0dd\uac01\uc740 \ub0ac",
                "\ub3c4\ubc15 \uc0dd\uac01\uc774 \ub0ac"
        )) {
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
                "도박 생각이 나",
                "도박 생각이 조금 나",
                "\ub2e4\uc2dc \ud558\uace0 \uc2f6",
                "\ub610 \ud558\uace0 \uc2f6",
                "\ub354 \ud558\uace0 \uc2f6\uc5b4\uc9c0",
                "\uc0ac\uc774\ud2b8 \ub4e4\uc5b4\uac00\uace0 \uc2f6\uc740 \ub9c8\uc74c",
                "\ud55c\ubc88\ub9cc \ud574\ubcfc\uae4c",
                "\ud55c \ubc88\ub9cc \ud574\ubcfc\uae4c",
                "\ud574\ubcfc\uae4c \uc2f6\uc5b4\uc11c",
                "\ub2e4\uc2dc \ud574\ubcfc\uae4c \ud558\ub294 \ub9c8\uc74c",
                "\ud574\ubcfc\uae4c \ud558\ub294 \ub9c8\uc74c"
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
                "\ud55c\ubc88\uc5d0 \ub9cc\ud68c\ud558\uace0 \uc2f6",
                "\ub9cc\ud68c\ud574\uc57c\uaca0\ub2e4\ub294 \uc0dd\uac01",
                "\ub9cc\ud68c\ud574\uc57c\uaca0\ub2e4",
                "\ub9cc\ud68c\ud574\uc57c\uaca0"
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
        if (containsCompletedSlotGambling(text)) {
            return true;
        }

        if (containsCompletedWagerWithNormalOrder(text)) {
            return true;
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

        boolean gamblingCheckoutContext =
                containsAny(
                        text,
                        "\ub3c4\ubc15",
                        "\ubca0\ud305",
                        "\uce74\uc9c0\ub178",
                        "\uc2ac\ub86f"
                )
                && containsAny(
                        text,
                        "\uacb0\uc81c \uc9c1\uc804"
                );

        if (gamblingCheckoutContext) {
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
                "스포츠토토 사이트",
                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8",
                "\uc608\uc804\uc5d0 \ud558\ub358 \uc0ac\uc774\ud2b8",
                "\uc608\uc804\uc5d0 \uc0ac\uc6a9\ud558\ub358 \uc0ac\uc774\ud2b8"
        );

        boolean blockedAccountFundingContext =
                containsAny(text, "\uacc4\uc815 \ub9c9\uc544\ub193", "\uacc4\uc815\uc744 \ub9c9\uc544\ub193")
                && containsAny(text, "\uc785\uae08 \ubc84\ud2bc");

        boolean repeatedFundingReentryContext =
                containsAny(
                        text,
                        "\ub2e4\uc2dc \ub4e4\uc5b4\uac14",
                        "\ub2e4\uc2dc \ub4e4\uc5b4\uac14\uc5b4\uc694"
                )
                && containsAny(
                        text,
                        "\uc785\uae08\ud558\uace0",
                        "\uc785\uae08\ud558\uace0 \ub098\ub2c8\uae4c"
                )
                && containsAny(
                        text,
                        "\ub610 \ub85c\uadf8\uc778",
                        "\ub2e4\uc74c \ub0a0 \ub610 \ub85c\uadf8\uc778"
                );

        boolean reentryCompleted = containsAny(
                text,
                "\ub2e4\uc2dc \ub4e4\uc5b4\uac00\uac8c \ub410",
                "\ub2e4\uc2dc \ub4e4\uc5b4\uac00\uac8c \ub418",
                "\ub2e4\uc2dc \ucc3e\uc544\ubd24",
                "들어가 본",
                "\uc8fc\uc18c\ub97c \uac80\uc0c9\ud558\uace0 \ud654\uba74\uc744",
                "\ub2e4\uc2dc \ub4e4\uc5b4\uac14",
                "\uac80\uc0c9\ud558\ub2e4\uac00 \uacb0\uad6d \ub4e4\uc5b4\uac14",
                "\uac80\uc0c9 \uacb0\uacfc\ub97c \uba87 \uac1c \ud655\uc778",
                "\ud55c \uacf3\uc744 \ub20c\ub7ec\ubcf4\ub2c8 \uc2e4\uc81c \ud654\uba74\uae4c\uc9c0 \uc5f4\ub824",
                "\ud55c \uacf3\uc744 \ub20c\ub7ec\ubd24",
                "\uc2e4\uc81c \ud654\uba74\uae4c\uc9c0 \uc5f4\ub824"
        );

        return (
                gamblingSiteContext
                || blockedAccountFundingContext
                || repeatedFundingReentryContext
        )
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

        boolean genericGamblingSiteSearch =
                containsAny(
                        text,
                        "\uc0ac\uc774\ud2b8\ub9cc \uac80\uc0c9",
                        "\uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9",
                        "\uac80\uc0c9\uc740 \ud588"
                )
                && containsAny(
                        text,
                        "\ub3c4\ubc15 \uc0dd\uac01",
                        "\ubca0\ud305 \uc0dd\uac01",
                        "\uc2ac\ub86f",
                        "\uce74\uc9c0\ub178"
                );

        boolean relatedSiteAccessAfterSportsContext =
                containsAny(
                        text,
                        "\uc2a4\ud3ec\uce20 \uacbd\uae30",
                        "\uacbd\uae30 \uacb0\uacfc"
                )
                && containsAny(
                        text,
                        "\ubca0\ud305\uc740 \ud558\uc9c0 \uc54a",
                        "\ub3c8\uc744 \uac78\uc9c0 \uc54a"
                )
                && containsAny(
                        text,
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \uba87 \uad70\ub370 \ub20c\ub7ec",
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \ub20c\ub7ec",
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \ub20c\ub7ec\ubcf4"
                );

        boolean typedThenDeletedSearch =
                (
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
                                "\ucce4\ub2e4\uac00",
                                "\uc785\ub825\ud558\ub824\ub2e4\uac00"
                        )
                        && containsAny(
                                text,
                                "\uc9c0\uc6e0",
                                "\uc0ad\uc81c\ud588"
                        )
                )
                || (
                        containsAny(
                                text,
                                "\uac80\uc0c9\uc5b4\ub97c \ud558\ub098 \uc785\ub825\ud558\uae34 \ud588",
                                "\uac80\uc0c9\uc5b4\ub97c \uc785\ub825\ud558\uae34 \ud588",
                                "\uac80\uc0c9\uc5b4\ub97c \uc785\ub825\ud588"
                        )
                        && containsAny(
                                text,
                                "\uacb0\uacfc\uac00 \ub728\uae30 \uc804\uc5d0 \uc9c0\uc6e0",
                                "\uac80\uc0c9 \uacb0\uacfc\uac00 \ub728\uae30 \uc804\uc5d0 \uc9c0\uc6e0"
                        )
                        && containsAny(
                                text,
                                "\ubca0\ud305\uae4c\uc9c0 \uac04 \uac74 \uc544\ub2c8",
                                "\ubca0\ud305\uae4c\uc9c0 \uac04 \uac83\uc740 \uc544\ub2c8"
                        )
                )
                || (
                        containsAny(
                                text,
                                "\uac80\uc0c9\ucc3d\uc5d0 \ub4e4\uc5b4\uac00\uc11c"
                        )
                        && containsAny(
                                text,
                                "\uba87 \uae00\uc790\ub97c \uc37c\ub2e4\uac00",
                                "\uba87 \uae00\uc790\ub97c \uc37c"
                        )
                        && containsAny(
                                text,
                                "\uc9c0\uc6e0"
                        )
                        && containsAny(
                                text,
                                "\ub2e4\uc2dc \uc4f0\uc9c0\ub294 \uc54a\uc558",
                                "\ub85c\uadf8\uc778 \uc815\ubcf4\ub3c4 \uc785\ub825\ud558\uc9c0 \uc54a\uc558"
                        )
                );

        boolean gamblingAppSearchAborted =
                containsAny(
                        text,
                        "\uc2ac\ub86f",
                        "\ub3c4\ubc15",
                        "\ubca0\ud305",
                        "\uce74\uc9c0\ub178"
                )
                && (
                        (
                                containsAny(
                                        text,
                                        "\uc571\uc744 \ucc3e\ub2e4\uac00",
                                        "\uc571\uc744 \ucc3e\uc558"
                                )
                                && containsAny(
                                        text,
                                        "\uc124\uce58\ub294 \uc548",
                                        "\uc124\uce58\ud558\uc9c0 \uc54a"
                                )
                        )
                        || (
                                containsAny(
                                        text,
                                        "\uc571\uc774 \uc544\uc9c1\ub3c4 \uc788\ub294\uc9c0 \ucc3e\uc544\ubd24",
                                        "\uc571\uc774 \uc544\uc9c1\ub3c4 \uc788\ub294\uc9c0 \ucc3e"
                                )
                                && containsAny(
                                        text,
                                        "\uac80\uc0c9 \uacb0\uacfc\uc5d0\uc11c \uc571 \uc774\ub984\uc774 \ubcf4\uc774"
                                )
                                && containsAny(
                                        text,
                                        "\ub20c\ub7ec\uc11c \uc815\ubcf4\ub9cc \uc870\uae08 \ubcf4\uace0 \ub098\uc654",
                                        "\uc815\ubcf4\ub9cc \uc870\uae08 \ubcf4\uace0 \ub098\uc654"
                                )
                        )
                );

        boolean sportsOddsPreBettingSearch =
                containsAny(
                        text,
                        "\uacbd\uae30 \uc77c\uc815",
                        "\uc5b4\ub5a4 \ud300\uc774 \uc774\uae38\uc9c0"
                )
                && containsAny(
                        text,
                        "\ubc30\ub2f9\ub3c4 \ubcf4",
                        "\ubc30\ub2f9\uc744 \ubcf4"
                )
                && containsAny(
                        text,
                        "\uc608\uc804\uc5d0 \ud588\ub358 \ubc29\uc2dd",
                        "\uc608\uc804 \ubc29\uc2dd\uacfc \ube44\uc2b7"
                );

        boolean priorPlaceSearchWithLogin =
                containsAny(
                        text,
                        "\uc608\uc804\uc5d0 \ubcf4\ub358 \uacf3"
                )
                && containsAny(
                        text,
                        "\uac80\uc0c9\ud588\uc2b5\ub2c8\ub2e4",
                        "\uac80\uc0c9\ud588"
                )
                && containsAny(
                        text,
                        "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588"
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
                || genericGamblingSiteSearch
                || relatedSiteAccessAfterSportsContext
                || typedThenDeletedSearch
                || gamblingAppSearchAborted
                || sportsOddsPreBettingSearch
                || priorPlaceSearchWithLogin;
    }

    private boolean containsLoginScreenEntryAttempt(
            String text
    ) {

        boolean accountContext = containsAny(
                text,
                "\uacc4\uc815",
                "\uc0ac\uc774\ud2b8",
                "\ub3c4\ubc15 \uc571"
        );

        boolean loginScreenEntered = containsAny(
                text,
                "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac00",
                "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \ub4e4\uc5b4\uac14",
                "\ub85c\uadf8\uc778 \ud654\uba74\uc5d0 \ub4e4\uc5b4\uac00",
                "\ub85c\uadf8\uc778 \ud654\uba74\uc5d0 \ub4e4\uc5b4\uac14",
                "\uc0ac\uc774\ud2b8 \ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \uac14",
                "\ub85c\uadf8\uc778 \ud654\uba74\uae4c\uc9c0 \uac14",
                "\ub85c\uadf8\uc778 \ud654\uba74\uc5d0\uc11c \ud55c\ucc38 \uc788",
                "\ub85c\uadf8\uc778 \ud654\uba74\uc774 \ub098\uc624\uc790",
                "\ub85c\uadf8\uc778 \ud654\uba74\uc774 \ub098\uc640"
        );

        boolean passwordEntryConsideredThenClosed =
                containsAny(
                        text,
                        "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\ub824\ub2c8",
                        "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\ub824\uace0"
                )
                && containsAny(
                        text,
                        "\uadf8\ub0e5 \ub2eb\uc558",
                        "\uadf8\ub0e5 \ub2eb\uc558\uc2b5\ub2c8\ub2e4",
                        "\ud654\uba74\uc744 \ub2eb\uc558"
                );

        boolean loginNotCompleted = containsAny(
                text,
                "\uc544\uc774\ub514\ub791 \ube44\ubc00\ubc88\ud638\ub294 \uc785\ub825\ud558\uc9c0 \uc54a\uc558",
                "\uc544\uc774\ub514\uc640 \ube44\ubc00\ubc88\ud638\ub294 \uc785\ub825\ud558\uc9c0 \uc54a\uc558",
                "\uc544\uc774\ub514\ub098 \ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\uac70\ub098 \ub3c8\uc744 \ub123\uc9c0\ub294 \uc54a",
                "\ub85c\uadf8\uc778\ud558\uc9c0 \uc54a\uc558",
                "\uc2e4\uc81c \ub85c\uadf8\uc778\uc740 \ud558\uc9c0 \uc54a\uc558",
                "\ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub193",
                "\ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub1a8",
                "\uc804\ud654\uac00 \uc640\uc11c \uadf8 \ub4a4\ub85c\ub294 \ubabb \ud588",
                "\ub354 \uc9c4\ud589\ud558\uc9c0 \uc54a\uace0",
                "\ube0c\ub77c\uc6b0\uc800\ub97c \ub2eb\uc558"
        )
                || passwordEntryConsideredThenClosed;

        return accountContext
                && loginScreenEntered
                && loginNotCompleted;

    }

    private boolean containsProtectiveBlock(String text) {

        boolean bettingAmountPhoneInterruption =
                containsAny(
                        text,
                        "베팅 앱",
                        "베팅앱"
                )
                && containsAny(
                        text,
                        "금액을 입력하는 데까지 갔",
                        "금액 입력하는 데까지 갔"
                )
                && containsAny(
                        text,
                        "전화가 와서 중단",
                        "전화가 와서"
                );

        if (bettingAmountPhoneInterruption) {
            return false;
        }

        boolean recoveryPhoneLookupWithoutCall =
                containsAny(
                        text,
                        "상담을 받아볼까",
                        "상담 받아볼까"
                )
                && containsAny(
                        text,
                        "번호를 검색해서",
                        "전화번호를 검색해서",
                        "번호를 검색했"
                )
                && containsAny(
                        text,
                        "전화를 걸지는 못했",
                        "전화를 걸지 못했",
                        "전화는 걸지 않았"
                );

        if (recoveryPhoneLookupWithoutCall) {
            return false;
        }
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

        if (
                (
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
                                "\ucce4\ub2e4\uac00",
                                "\uc785\ub825\ud588\ub2e4\uac00"
                        )
                        && containsAny(
                                text,
                                "\uc9c0\uc6e0",
                                "\uc0ad\uc81c\ud588"
                        )
                )
                || (
                        containsAny(
                                text,
                                "\uac80\uc0c9\uc5b4\ub97c \ud558\ub098 \uc785\ub825\ud558\uae34 \ud588",
                                "\uac80\uc0c9\uc5b4\ub97c \uc785\ub825\ud558\uae34 \ud588",
                                "\uac80\uc0c9\uc5b4\ub97c \uc785\ub825\ud588"
                        )
                        && containsAny(
                                text,
                                "\uacb0\uacfc\uac00 \ub728\uae30 \uc804\uc5d0 \uc9c0\uc6e0",
                                "\uac80\uc0c9 \uacb0\uacfc\uac00 \ub728\uae30 \uc804\uc5d0 \uc9c0\uc6e0"
                        )
                        && containsAny(
                                text,
                                "\ubca0\ud305\uae4c\uc9c0 \uac04 \uac74 \uc544\ub2c8",
                                "\ubca0\ud305\uae4c\uc9c0 \uac04 \uac83\uc740 \uc544\ub2c8"
                        )
                )
                || (
                        containsAny(
                                text,
                                "\uac80\uc0c9\ucc3d\uc5d0 \ub4e4\uc5b4\uac00\uc11c"
                        )
                        && containsAny(
                                text,
                                "\uba87 \uae00\uc790\ub97c \uc37c\ub2e4\uac00",
                                "\uba87 \uae00\uc790\ub97c \uc37c"
                        )
                        && containsAny(
                                text,
                                "\uc9c0\uc6e0"
                        )
                        && containsAny(
                                text,
                                "\ub2e4\uc2dc \uc4f0\uc9c0\ub294 \uc54a\uc558",
                                "\ub85c\uadf8\uc778 \uc815\ubcf4\ub3c4 \uc785\ub825\ud558\uc9c0 \uc54a\uc558"
                        )
                )
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

        boolean explicitGenericWorkSiteContext =
                containsAny(
                        text,
                        "회사에서 쓰는 업무 사이트",
                        "업무 사이트"
                )
                && !containsAny(
                        text,
                        "도박",
                        "베팅",
                        "카지노",
                        "슬롯",
                        "스포츠토토"
                );

        if (explicitGenericWorkSiteContext) {
            return false;
        }

        boolean explicitGenericGameAppContext =
                containsAny(
                        text,
                        "예전에 꽤 자주 하던 게임",
                        "예전에 자주 하던 게임"
                )
                && containsAny(
                        text,
                        "앱을 다시 설치",
                        "다시 설치했습니다",
                        "재설치"
                )
                && containsAny(
                        text,
                        "실행까지는 했",
                        "실행까지 했",
                        "실행했"
                )
                && containsAny(
                        text,
                        "로그인은 하지 않고",
                        "로그인하지 않고"
                )
                && containsAny(
                        text,
                        "앱을 지웠",
                        "앱을 삭제"
                )
                && !containsAny(
                        text,
                        "도박",
                        "베팅",
                        "카지노",
                        "슬롯",
                        "스포츠토토"
                );

        if (explicitGenericGameAppContext) {
            return false;
        }

        boolean genericCheckoutWithoutGamblingDomain =
                containsAny(
                        text,
                        "\uacb0\uc81c \uc9c1\uc804"
                )
                && !containsAny(
                        text,
                        "\ub3c4\ubc15",
                        "\ubca0\ud305",
                        "\uce74\uc9c0\ub178",
                        "\uc2ac\ub86f"
                );

        if (genericCheckoutWithoutGamblingDomain) {
            return false;
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
                "\uc571\uc744 \ub2e4\uc2dc \uc9c0",
                "휴대폰을 내려놓",
                "마지막에 멈췄",
                "결제를 멈췄",
                "결제를 취소",
                "시도했지만 취소",
                "차단했",
                "차단하고",
                "\ucc28\ub2e8\ud574 \ub193",
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

        boolean gamblingCounselingAppointmentBooked =
                (
                        containsAny(
                                text,
                                "\uc2e4\uc81c\ub85c \uc5f0\ub77d\ud55c \uac74 \uc774\ubc88\uc774 \ucc98\uc74c",
                                "\uc2e4\uc81c\ub85c \uc5f0\ub77d\ud55c \uac83\uc740 \uc774\ubc88\uc774 \ucc98\uc74c"
                        )
                        && containsAny(
                                text,
                                "\ub3c4\ubc15 \ub54c\ubb38\uc5d0 \ubb38\uc81c\uac00 \uc0dd\uacbc\ub2e4\uace0 \ub9d0",
                                "\ub3c4\ubc15 \ubb38\uc81c\uac00 \uc0dd\uacbc\ub2e4\uace0 \ub9d0"
                        )
                        && containsAny(
                                text,
                                "\uc608\uc57d\uc744 \uc7a1\uc558",
                                "\uc0c1\ub2f4 \uc608\uc57d\uc744 \uc7a1\uc558"
                        )
                )
                || (
                        containsAny(
                                text,
                                "\ub3c4\ubc15",
                                "\ubca0\ud305",
                                "\uce74\uc9c0\ub178",
                                "\uc2ac\ub86f"
                        )
                        && containsAny(
                                text,
                                "\uc2e4\uc81c\ub85c \uc804\ud654\ud574\uc11c",
                                "\uc2e4\uc81c\ub85c \uc804\ud654\ud588",
                                "\uc804\ud654\ud574\uc11c"
                        )
                        && containsAny(
                                text,
                                "\uc0c1\ub2f4 \uc77c\uc815\uc744 \uc7a1",
                                "\uc0c1\ub2f4 \uc608\uc57d\uc744 \uc7a1",
                                "\uc608\uc57d\uc744 \uc7a1"
                        )
                );

        boolean gamblingFamilyDisclosureSupportSeeking =

                containsAny(

                        text,

                        "\ub3c4\ubc15 \ub54c\ubb38\uc5d0 \ud06c\uac8c \uc2f8\uc6b4",
                        "\ub3c4\ubc15 \ub54c\ubb38\uc5d0"

                )

                && containsAny(

                        text,

                        "\uad00\ub828 \uc5f0\ub77d\ucc98\ub97c \ucc3e\uc544\ubd24",
                        "\uc5f0\ub77d\ucc98\ub97c \ucc3e\uc544\ubd24"

                )

                && containsAny(

                        text,

                        "\ud63c\uc790 \uace0\ubbfc\ud558\uc9c0 \ub9d0\uc790",
                        "\ud63c\uc790 \uace0\ubbfc\ud558\uc9c0 \ub9d0"

                )

                && containsAny(

                        text,

                        "\ub204\ub098\ud55c\ud14c \uba3c\uc800 \uc598\uae30\ud588\uc2b5\ub2c8\ub2e4",
                        "\ub204\ub098\ud55c\ud14c \uba3c\uc800 \uc598\uae30\ud588",
                        "\ub204\ub098\uc5d0\uac8c \uba3c\uc800 \uc598\uae30\ud588"

                );

        boolean gamblingSiteBlockCompleted =
                containsAny(
                        text,
                        "\ub3c4\ubc15",
                        "\ubca0\ud305",
                        "\uce74\uc9c0\ub178",
                        "\uc2ac\ub86f",
                        "\uad00\ub828 \uc0ac\uc774\ud2b8"
                )
                && containsAny(
                        text,
                        "\uc774\ud2c0 \ub4a4",
                        "\uba70\uce60 \ub4a4",
                        "\ub2e4\uc74c \ub0a0"
                )
                && containsAny(
                        text,
                        "\uc0ac\uc774\ud2b8\uac00 \ub2e4\uc2dc \ub098\uc624\uc9c0 \uc54a\ub3c4\ub85d \ucc28\ub2e8",
                        "\uad00\ub828 \uc0ac\uc774\ud2b8\ub97c \ucc28\ub2e8",
                        "\uc0ac\uc774\ud2b8\ub97c \ucc28\ub2e8"
                );

        boolean recoveryPhoneCall =
                containsAny(text, "전화했")
                && !containsAny(text, "고객센터");

        boolean triggerAvoidanceMessage =
                containsAny(
                        text,
                        "\uce5c\uad6c\ud55c\ud14c"
                )
                && containsAny(
                        text,
                        "\ub2f9\ubd84\uac04 \uadf8\ub7f0 \uc598\uae30\ub294 \ud558\uc9c0 \ub9d0\ub77c\uace0"
                )
                && containsAny(
                        text,
                        "\uba54\uc2dc\uc9c0\ub97c \ubcf4\ub0c8"
                );

        return gamblingCounselingAppointmentBooked
                || gamblingFamilyDisclosureSupportSeeking
                || gamblingSiteBlockCompleted
                || recoveryPhoneCall
                || triggerAvoidanceMessage
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
        if (containsCompletedSlotGambling(text)) {
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
                "\ud55c \ubc88 \ubca0\ud305\ud588\uace0",
                "\uae08\uc561\uc744 \uac78\uc5c8\ub294\ub370",
                "\uccab \ubc88\uc9f8 \uacbd\uae30\uc5d0 \uae08\uc561\uc744 \uac78",
                "\uae08\uc561\uc744 \ub123\uc5b4 \ubca0\ud305\ud588",
                "\ub2e4\ub978 \uacbd\uae30\uae4c\uc9c0 \uae08\uc561\uc744 \ub123\uc5b4 \ubca0\ud305",
                "\uae08\uc561\uc744 \uc785\ub825\ud588\uace0",
                "\ubca0\ud305\uc744 \uc81c\ucd9c",
                "\ubca0\ud305\uae4c\uc9c0 \uc644\ub8cc",
                "\ubca0\ud305\uc744 \uc644\ub8cc",
                "\ubca0\ud305\uc774 \uc644\ub8cc\ub41c \uc0c1\ud0dc",
                "\uc774\ubbf8 \ubca0\ud305\uc774 \uc644\ub8cc\ub41c \uc0c1\ud0dc"
        );

        boolean orderCompleted = containsAny(
                text,
                "\uc8fc\ubb38\ub3c4 \uc815\uc0c1\uc801\uc73c\ub85c \ucc98\ub9ac\ub410",
                "\uc8fc\ubb38\uc774 \uc815\uc0c1\uc801\uc73c\ub85c \ucc98\ub9ac\ub410",
                "\uc8fc\ubb38\ub3c4 \uc815\uc0c1 \ucc98\ub9ac\ub410",
                "\ubca0\ud305\uc740 \uc815\uc0c1\uc801\uc73c\ub85c \ucc98\ub9ac\ub3fc \uc788",
                "\ubca0\ud305\uc774 \uc815\uc0c1\uc801\uc73c\ub85c \ucc98\ub9ac\ub3fc \uc788",
                "\ub450 \uac74 \ubaa8\ub450 \ub05d\ub09c \ub4a4",
                "\ub450 \uac74 \ubaa8\ub450 \ub05d\ub09c",
                "\ub450\uac74 \ubaa8\ub450 \ub05d\ub09c",
                "\ub3c8\uc744 \uc783\uc5c8",
                "\ub3c8\uc744 \uc783\uc5c8\ub2e4",
                "\ubca0\ud305\uae4c\uc9c0 \uc644\ub8cc",
                "\ubca0\ud305\uc744 \uc644\ub8cc",
                "\ubca0\ud305\uc774 \uc644\ub8cc\ub41c \uc0c1\ud0dc",
                "\uc774\ubbf8 \ubca0\ud305\uc774 \uc644\ub8cc\ub41c \uc0c1\ud0dc"
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

    private boolean containsCompletedSlotGambling(
            String text
    ) {
        boolean directSlotCompletion = containsAny(
                text,
                "\uc2ac\ub86f\uc744 \uc880 \ud558\ub2e4\uac00",
                "\uc2ac\ub86f\uc744 \ud558\ub2e4\uac00",
                "\uc2ac\ub86f \uc880 \ud558\ub2e4\uac00",
                "\uc2ac\ub86f\uc744 \uba87 \ubc88 \ub3cc\ub838",
                "\uc2ac\ub86f\uc744 \uba87\ubc88 \ub3cc\ub838"
        );

        boolean slotAppMultipleWagersCompleted =
                containsAny(
                        text,
                        "\uc2ac\ub86f \uc571",
                        "\uc2ac\ub86f\uc571"
                )
                && containsAny(
                        text,
                        "\uc2e4\uc81c\ub85c \ubca0\ud305\uc744 \uba87 \ubc88 \ud588",
                        "\uc2e4\uc81c\ub85c \ubca0\ud305\uc744 \uba87\ubc88 \ud588",
                        "\ubca0\ud305\uc744 \uba87 \ubc88 \ud588",
                        "\ubca0\ud305\uc744 \uba87\ubc88 \ud588"
                );

        return directSlotCompletion
                || slotAppMultipleWagersCompleted;
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

        boolean repeatedReentryContext =
                containsAny(
                        text,
                        "\ub2e4\uc2dc \ub4e4\uc5b4\uac14",
                        "\ub2e4\uc2dc \ub4e4\uc5b4\uac14\uc5b4\uc694"
                )
                && containsAny(
                        text,
                        "\ub610 \ub85c\uadf8\uc778",
                        "\ub2e4\uc74c \ub0a0 \ub610 \ub85c\uadf8\uc778"
                );

        boolean loginCompleted = containsAny(
                text,
                "\ub85c\uadf8\uc778\ud558\uace0",
                "\ub85c\uadf8\uc778\ud588\uace0",
                "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588",
                "\ub610 \ub85c\uadf8\uc778",
                "\ub85c\uadf8\uc778\ud588\uc2b5\ub2c8\ub2e4"
        );

        boolean fundingCompleted = containsAny(
                text,
                "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub194",
                "\ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub194",
                "\ub3c8\uc744 \uc62e\uaca8\ub194",
                "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub1a8",
                "\uacc4\uc88c\uc5d0\uc11c \ub3c8\uae4c\uc9c0 \uc62e\uaca8\ub1a8\uc5b4",
                "\ub3c8\uc744 \ub123\uc5c8",
                "\uc785\uae08\ud588",
                "\uc785\uae08\ud558\uace0",
                "\uc785\uae08\ud558\uace0 \ub098\ub2c8\uae4c"
        );

        boolean wagerCompleted = containsAny(
                text,
                "\ubca0\ud305\ud588",
                "\ub3c8\uc744 \uac78\uc5c8"
        );

        return (priorSiteContext || repeatedReentryContext)
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
                "\uce74\uc9c0\ub178 \uc0ac\uc774\ud2b8",
                "\uc608\uc804\uc5d0 \ubcf4\ub358 \uacf3",
                "\uadf8\ub54c \uc4f0\ub358 \uacc4\uc815"
        );

        boolean blockedAccountFundingContext =
                containsAny(text, "\uacc4\uc815 \ub9c9\uc544\ub193", "\uacc4\uc815\uc744 \ub9c9\uc544\ub193")
                && containsAny(text, "\uc785\uae08 \ubc84\ud2bc");

        boolean loginCompleted = containsAny(
                text,
                "\ub85c\uadf8\uc778\uae4c\uc9c0 \ud588",
                "로그인까지는 했",
                "\ub85c\uadf8\uc778\ud588\uace0",
                "\ub85c\uadf8\uc778\ud574\uc11c",
                "\ub85c\uadf8\uc778\ud588\uc5b4\uc694",
                "\ub85c\uadf8\uc778\ub9cc \ud574\ubd24\uc5b4\uc694",
                "\ub85c\uadf8\uc778\ub9cc \ud574\ubd24",
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
