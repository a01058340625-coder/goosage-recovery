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
                        "베팅",
                        "배팅",
                        "돈을 걸",
                        "주문을 제출",
                        "베팅 버튼"
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

        if (
                containsAny(
                        text,
                        "검색",
                        "검색창",
                        "사이트 이름",
                        "앱 이름"
                )
        ) {
            return ActionType.SEARCH;
        }

        return ActionType.UNKNOWN;
    }

    private ActionStage resolveActionStage(
            String text,
            ActionType type
    ) {
        if (
                containsAny(
                        text,
                        "성공했",
                        "완료됐",
                        "성립됐",
                        "실제로 해제됐",
                        "실제로 입금했",
                        "실제로 베팅했"
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
                        "버튼을 눌렀"
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
                        "앱 이름을 입력"
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
