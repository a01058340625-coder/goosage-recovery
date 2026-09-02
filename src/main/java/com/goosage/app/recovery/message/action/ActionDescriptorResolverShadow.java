package com.goosage.app.recovery.message.action;

import java.util.ArrayList;
import java.util.List;

public class ActionDescriptorResolverShadow {

    public ActionDescriptor resolve(String message) {
        String normalized = normalize(message);

        if (normalized.isBlank()) {
            return unknown();
        }

        ActionType actionType =
                resolveActionType(normalized);

        ActionStage actionStage =
                resolveActionStage(
                        normalized,
                        actionType
                );

        boolean completed =
                actionStage == ActionStage.COMPLETED;

        List<String> evidence =
                collectEvidence(
                        normalized,
                        actionType,
                        actionStage
                );

        double confidence =
                actionType == ActionType.UNKNOWN
                        ? 0.20
                        : actionStage == ActionStage.UNKNOWN
                                ? 0.60
                                : 0.90;

        return new ActionDescriptor(
                actionType,
                actionStage,
                completed,
                confidence,
                evidence
        );
    }

    private ActionType resolveActionType(
            String text
    ) {
        if (
                (
                        text.contains("베팅한 적은 없")
                        || text.contains("베팅하지 않")
                        || text.contains("돈을 넣거나 베팅한 적은 없")
                )
                && (
                        text.contains("관련 앱을 찾")
                        || text.contains("앱을 찾")
                )
        ) {
            return ActionType.SEARCH;
        }

        if (
                text.contains("검색창")
                && (
                        text.contains("로그인 정보도 입력하지 않")
                        || text.contains("로그인 정보를 입력하지 않")
                )
        ) {
            return ActionType.SEARCH;
        }

        if (
                (
                        text.contains("검색어를 하나 입력")
                        || text.contains("검색어를 입력")
                )
                && (
                        text.contains("베팅까지 간 건 아니")
                        || text.contains("결과가 뜨기 전에 지우")
                        || text.contains("그 뒤로는 아무것도 하지 않")
                )
        ) {
            return ActionType.SEARCH;
        }

        if (
                (
                        text.contains("앱을 찾아보기")
                        || text.contains("앱을 찾아봤")
                        || text.contains("앱을 찾아보")
                )
                && (
                        text.contains("슬롯을 자주 했")
                        || text.contains("예전에는 슬롯")
                        || text.contains("지금은 끊")
                )
        ) {
            return ActionType.SEARCH;
        }

        if (
                text.contains("검색")
                && (
                        text.contains("로그인까지는 안 갔")
                        || text.contains("로그인하지 않았")
                        || text.contains("로그인은 하지 않았")
                        || text.contains("실제 로그인은 하지 않았")
                )
        ) {
            return ActionType.SEARCH;
        }

        if (
                (
                        text.contains("베팅은 안 하고")
                        || text.contains("베팅하지 않")
                        || text.contains("배팅은 안 하고")
                )
                && (
                        text.contains("찾아보")
                        || text.contains("찾아보게")
                        || text.contains("배당")
                        || text.contains("경기 일정")
                )
        ) {
            return ActionType.SEARCH;
        }

        if (
                containsAny(
                        text,
                        "상담센터",
                        "상담을 받",
                        "가족에게 말",
                        "카드를 맡",
                        "앱을 삭제",
                        "앱을 지우"
                )
        ) {
            return ActionType.RECOVERY;
        }

        if (
                containsAny(
                        text,
                        "차단 해제",
                        "계정을 막",
                        "계정 차단",
                        "해제 요청",
                        "해제 신청"
                )
        ) {
            return ActionType.ACCOUNT_CONTROL;
        }

        if (
                containsAny(
                        text,
                        "\uBCA0\uD305",
                        "\uBC30\uD305",
                        "\uCE74\uC9C0\uB178",
                        "\uC2AC\uB86F",
                        "\uB3C8\uC744 \uAC78",
                        "\uC8FC\uBB38\uC744 \uC81C\uCD9C",
                        "\uBCA0\uD305 \uBC84\uD2BC"
                )
        ) {
            return ActionType.WAGER;
        }

        if (
                containsAny(
                        text,
                        "입금",
                        "송금",
                        "이체",
                        "결제수단"
                )
        ) {
            return ActionType.FUNDING;
        }

        if (
                containsAny(
                        text,
                        "로그인",
                        "아이디",
                        "비밀번호"
                )
        ) {
            return ActionType.LOGIN;
        }

        // SEARCH must precede ACCESS.
        if (
                containsAny(
                        text,
                        "??",
                        "???",
                        "??? ??",
                        "??? ??",
                        "? ??",
                        "?? ????",
                        "?? ???",
                        "?? ?? ???",
                        "?? ???"
                )
        ) {
            return ActionType.SEARCH;
        }

        if (
                containsAny(
                        text,
                        "접속",
                        "사이트에 들어갔",
                        "화면을 열",
                        "링크를 눌렀",
                        "링크를 눌러봤"
                )
        ) {
            return ActionType.ACCESS;
        }

        return ActionType.UNKNOWN;
    }

    private ActionStage resolveActionStage(
            String text,
            ActionType type
    ) {
        if (
                type == ActionType.SEARCH
                && (
                        text.contains("관련 앱을 찾아서")
                        || text.contains("앱을 찾아서")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("몇 글자를 썼")
                        || text.contains("몇 글자를 쓰")
                        || text.contains("몇 글자 썼")
                )
        ) {
            return ActionStage.INPUT;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("검색창에 몇 글자 쳤")
                        || text.contains("검색창에 몇 글자를 쳤")
                )
        ) {
            return ActionStage.INPUT;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("검색어를 하나 입력")
                        || text.contains("검색어 하나를 입력")
                )
        ) {
            return ActionStage.INPUT;
        }

        if (
                type == ActionType.WAGER
                && (
                        text.contains("슬롯을 하다가")
                        || text.contains("슬롯을 했")
                )
        ) {
            return ActionStage.COMPLETED;
        }

        if (
                type == ActionType.WAGER
                && (
                        text.contains("모바일 카지노를 시작")
                        || text.contains("카지노를 시작")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.WAGER
                && (
                        text.contains("배팅할 곳부터 찾")
                        || text.contains("베팅할 곳부터 찾")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.WAGER
                && (
                        text.contains("스포츠베팅을 시작")
                        || text.contains("베팅을 시작")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.WAGER
                && (
                        text.contains("베팅 화면을 다시 열")
                        || text.contains("베팅 화면을 열")
                        || text.contains("베팅 화면에 들어갔")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("찾아보기까지는 했")
                        || text.contains("찾아보기까지 했")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("검색은 했")
                        || text.contains("검색했")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("찾아보고")
                        || text.contains("배당도 보")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("찾아보게 됩")
                        || text.contains("찾아보게 되")
                        || text.contains("찾아보게")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                type == ActionType.SEARCH
                && (
                        text.contains("검색했")
                        || text.contains("검색해봤")
                        || text.contains("검색하게 돼")
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                containsAny(
                        text,
                        "\uC131\uACF5\uD588",
                        "\uC644\uB8CC\uB410",
                        "\uC131\uB9BD\uB410",
                        "\uBCA0\uD305\uD558\uACE0",
                        "\uC2E4\uC81C\uB85C \uD574\uC81C\uB410",
                        "\uC2E4\uC81C\uB85C \uC785\uAE08\uD588",
                        "\uC2E4\uC81C\uB85C \uBCA0\uD305\uD588",
                        "\uBCA0\uD305\uD588\uC2B5\uB2C8\uB2E4",
                        "\uBCA0\uD305\uD588\uC5B4",
                        "\uBCA0\uD305\uD588\uB2E4",
                        "\uBA87 \uBC88 \uD574\uBD24",
                        "\uBCA0\uD305\uC561\uC774 \uCEE4\uC84C",
                        "\uC2A4\uD3EC\uCE20\uBCA0\uD305\uAE4C\uC9C0 \uC190\uB300\uACE0"
                )
        ) {
            return ActionStage.COMPLETED;
        }

        if (
                containsAny(
                        text,
                        "제출 버튼",
                        "제출했",
                        "요청 버튼",
                        "요청을 실행",
                        "버튼을 눌렀",
                        "버튼까지 눌렀",
                        "버튼까지 실제로 눌렀"
                )
        ) {
            return ActionStage.SUBMITTED;
        }

        if (
                containsAny(
                        text,
                        "금액을 입력",
                        "비밀번호를 입력",
                        "아이디를 입력",
                        "신청서를 작성",
                        "검색어를 입력",
                        "사이트 이름을 입력",
                        "앱 이름을 입력",
                        "앱을 찾아보기",
                        "앱을 찾아봤"
                )
        ) {
            return ActionStage.INPUT;
        }

        if (
                containsAny(
                        text,
                        "들어갔",
                        "열어봤",
                        "눌러봤",
                        "찾아봤",
                        "화면까지 들어가 봤",
                        "검색하게 돼",
                        "실행했",
                        "설치했"
                )
        ) {
            return ActionStage.STARTED;
        }

        if (
                containsAny(
                        text,
                        "할까",
                        "해볼까",
                        "하고 싶",
                        "생각이 들",
                        "고민했"
                )
        ) {
            return ActionStage.THOUGHT;
        }

        if (type == ActionType.UNKNOWN) {
            return ActionStage.UNKNOWN;
        }

        return ActionStage.UNKNOWN;
    }

    private List<String> collectEvidence(
            String text,
            ActionType type,
            ActionStage stage
    ) {
        List<String> result =
                new ArrayList<>();

        result.add(type.name());
        result.add(stage.name());

        return List.copyOf(result);
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

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.trim().replaceAll("\\s+", " ");
    }

    private ActionDescriptor unknown() {
        return new ActionDescriptor(
                ActionType.UNKNOWN,
                ActionStage.UNKNOWN,
                false,
                0.20,
                List.of()
        );
    }
}
