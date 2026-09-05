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
        // Explicit completed wager evidence has highest priority.
        if (
                containsAny(
                        text,
                        "돈을 걸었어",
                        "돈을 걸었다",
                        "돈을 걸었습니다",
                        "실제로 베팅을 했어",
                        "실제로 베팅했다",
                        "실제로 베팅했습니다",
                        "실제 베팅까지 한 번 성립됐어",
                        "실제 베팅이 성립됐어", "실제 베팅이 한 번 성립된 뒤", "다시 베팅이 성립된 뒤", "베팅은 정상적으로 처리돼 있었",
                        "베팅을 완료했어",
                        "베팅을 완료했다",
                        "베팅을 완료했습니다",
                        "결국 한 번 베팅했고",
                        "베팅은 정상적으로 처리됐어",
                        "제출까지 완료하고",
                        "결과는 생각보다 빨리 나왔고",
                        "베팅이 완료된 것을 확인",
                        "정상적으로 접수됐다는 표시",
                        "접수됐다는 표시가 떠서 결과를 기다렸"
                )
        ) {
            return ActionType.WAGER;
        }

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
                        "결제수단을 가족에게 맡",
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

        /*
         * OOS_CASINO_LINK_ACCESS_PRECEDENCE_V1
         *
         * Concrete casino-link execution is ACCESS, not WAGER.
         *
         * Explicit completed/progressed wager evidence above retains
         * higher priority. This branch only handles actual link execution.
         */
        if (
                text.contains("\uCE74\uC9C0\uB178")
                && containsAny(
                        text,
                        "\uB9C1\uD06C\uB97C \uB20C\uB800",
                        "\uB9C1\uD06C\uB97C \uB20C\uB7EC",
                        "\uB9C1\uD06C\uB97C \uD074\uB9AD",
                        "\uB9C1\uD06C\uB97C \uC5F4\uC5B4"
                )
                && !containsAny(
                        text,
                        "\uB204\uB974\uC9C0 \uC54A",
                        "\uB20C\uB7EC\uC9C0 \uC54A",
                        "\uD074\uB9AD\uD558\uC9C0 \uC54A",
                        "\uC5F4\uC9C0 \uC54A"
                )
        ) {
            return ActionType.ACCESS;
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
                        "\uBCA0\uD305 \uBC84\uD2BC", "걸었어요", "걸었다", "베팅했어요", "베팅했다", "베팅했습니다"
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
                  text.contains("사이트 주소를 다시 검색")
                  || text.contains("사이트 주소를 검색")
          ) {
              return ActionType.SEARCH;
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
                        "검색창",
                        "검색어",
                        "검색",
                        "사이트 이름",
                        "앱 이름",
                        "자동완성",
                        "찾아봤",
                        "찾아보"
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
                type == ActionType.WAGER
                && containsAny(
                        text,
                        "돈을 걸었어",
                        "돈을 걸었다",
                        "돈을 걸었습니다",
                        "실제로 베팅을 했어",
                        "실제로 베팅했다",
                        "실제로 베팅했습니다",
                        "실제 베팅까지 한 번 성립됐어",
                        "실제 베팅이 성립됐어", "실제 베팅이 한 번 성립된 뒤", "다시 베팅이 성립된 뒤", "베팅은 정상적으로 처리돼 있었",
                        "베팅을 완료했어",
                        "베팅을 완료했다",
                        "베팅을 완료했습니다",
                        "결국 한 번 베팅했고",
                        "베팅은 정상적으로 처리됐어",
                        "제출까지 완료하고",
                        "결과는 생각보다 빨리 나왔고",
                        "베팅이 완료된 것을 확인",
                        "정상적으로 접수됐다는 표시",
                        "접수됐다는 표시가 떠서 결과를 기다렸"
                )
        ) {
            return ActionStage.COMPLETED;
        }

        /*
         * OOS_ACCESS_LINK_EXECUTION_STAGE_V1
         *
         * Concrete ACCESS link execution is STARTED.
         */
        if (
                type == ActionType.ACCESS
                && containsAny(
                        text,
                        "\uB9C1\uD06C\uB97C \uB20C\uB800",
                        "\uB9C1\uD06C\uB97C \uB20C\uB7EC",
                        "\uB9C1\uD06C\uB97C \uD074\uB9AD",
                        "\uB9C1\uD06C\uB97C \uC5F4\uC5B4"
                )
                && !containsAny(
                        text,
                        "\uB204\uB974\uC9C0 \uC54A",
                        "\uB20C\uB7EC\uC9C0 \uC54A",
                        "\uD074\uB9AD\uD558\uC9C0 \uC54A",
                        "\uC5F4\uC9C0 \uC54A"
                )
        ) {
            return ActionStage.STARTED;
        }


        /*
         * OOS_STAGE_WAGER_PRE_COMPLETION_V1
         *
         * Concrete wager progression before completion:
         *
         * - wager order/button executed -> SUBMITTED
         * - wager amount entered         -> INPUT
         *
         * These must run before generic THOUGHT/fallback rules,
         * while explicit completed-wager evidence above remains
         * higher priority.
         */
        if (
                type == ActionType.WAGER
                && containsAny(
                        text,
                        "\ubca0\ud305 \uc8fc\ubb38\uc744 \ub20c\ub800",
                        "\ubca0\ud305 \uc8fc\ubb38 \ubc84\ud2bc\uc744 \ub20c\ub800",
                        "\ubca0\ud305 \ubc84\ud2bc\uc744 \ub20c\ub800",
                        "\ubca0\ud305 \ubc84\ud2bc\uc744 \ub20c\ub7ec"
                )
        ) {
            return ActionStage.SUBMITTED;
        }

        if (
                type == ActionType.WAGER
                && containsAny(
                        text,
                        "\ubca0\ud305 \uae08\uc561\uae4c\uc9c0 \uc785\ub825",
                        "\ubca0\ud305 \uae08\uc561\uc744 \uc785\ub825",
                        "\ubca0\ud305\ud560 \uae08\uc561\uc744 \uc785\ub825",
                        "\uae08\uc561\uae4c\uc9c0 \uc785\ub825"
                )
        ) {
            return ActionStage.INPUT;
        }



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
                        || text.contains("슬롯을 좀 하다가")
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
                        
                        "\uC2E4\uC81C\uB85C \uD574\uC81C\uB410",
                        "\uC2E4\uC81C\uB85C \uC785\uAE08\uD588",
                        "\uC2E4\uC81C\uB85C \uBCA0\uD305\uD588",
                        "\uBCA0\uD305\uD588\uC2B5\uB2C8\uB2E4",
                        "\uBCA0\uD305\uD588\uC5B4",
                        "\uBCA0\uD305\uD588\uB2E4",
                        "\uBA87 \uBC88 \uD574\uBD24",
                        "\uBCA0\uD305\uC561\uC774 \uCEE4\uC84C", "걸었어요", "걸었다", "베팅했어요", "베팅했다", "베팅했습니다",
                        "\uC2A4\uD3EC\uCE20\uBCA0\uD305\uAE4C\uC9C0 \uC190\uB300\uACE0",
                        "베팅을 해버렸어",
                        "실제로 베팅을 하고",
                        "실제로 베팅을 했고",
                        "스포츠베팅을 했습니다",
                        "두 번째 베팅까지 끝내고",
                        "슬롯을 몇 차례 돌렸고",
                        "베팅까지 했고",
                        "실제로 베팅을 몇 번 했고",
                        "베팅까지 완료했다",
                        "베팅까지 넣었습니다",
                        "실제 베팅을 한 번 했습니다",
                        "이미 베팅이 완료된 상태였고",
                        "어제 건은 적중하지 않았고",
                        "슬롯을 몇 번 돌리고",
                        "한 경기에 베팅했고",
                        "한 번 돌려봤는데 결과가 뜨자마자",
                        "다시 베팅한 뒤",
                        "슬롯을 몇 번 돌렸는데 첫 결과",
                        "스포츠 베팅을 두 번 했는데",
                        "결제를 완료했어요",
                        "결제수단을 가족에게 맡겨두었습니다"
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
                        "사이트 이름을 입력", "로그인했고", "로그인했다", "로그인했습니다",
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

        /*
         * Stage fallback:
         * ActionType이 이미 결정된 경우,
         * 일반적인 실제 진행 표현을 Stage로 복원한다.
         * 기존 세부 Guard보다 뒤에서 동작한다.
         */

        if (containsAny(
                text,
                "검색창까지",
                "사이트 주소까지 검색",
                "검색해서 이름만 확인",
                "검색해봤어요",
                "검색해봤어",
                "검색했습니다",
                "검색했어요",
                "검색했어",
                "사이트 이름을 검색",
                "사이트를 검색"
        )) {
            return ActionStage.STARTED;
        }

        if (containsAny(
                text,
                "몇 글자 치",
                "몇 글자를 썼",
                "검색어를 하나 입력",
                "검색어를 입력",
                "검색창에 스포츠베팅이라고 치",
                "검색창에 사이트 이름을 치"
        )) {
            return ActionStage.INPUT;
        }

        if (containsAny(
                text,
                "로그인 화면까지",
                "로그인 화면에서",
                "로그인만 해봤",
                "로그인까지 했",
                "로그인했다"
        )) {
            return ActionStage.STARTED;
        }

        if (containsAny(
                text,
                "입금 화면까지",
                "입금 화면에서",
                "입금하려고",
                "입금까지 갔"
        )) {
            return ActionStage.STARTED;
        }

        if (containsAny(
                text,
                "베팅 화면까지",
                "베팅 화면에서",
                "베팅하려고",
                "베팅까지 갔",
                "베팅 금액까지",
                "베팅 버튼까지"
        )) {
            return ActionStage.STARTED;
        }

        if (containsAny(
                text,
                "버튼을 눌렀",
                "버튼까지 눌렀",
                "실제로 눌렀",
                "제출 버튼까지"
        )) {
            return ActionStage.SUBMITTED;
        }

        if (containsAny(
                text,
                "사이트에 들어가",
                "사이트에 접속",
                "링크를 눌러",
                "화면까지 봤",
                "화면만 봤",
                "화면을 봤"
        )) {
            return ActionStage.STARTED;
        }

        if (containsAny(
                text,
                "실제로 성립",
                "성립됐",
                "성립되었",
                "실제로 로그인",
                "실제로 입금"
        )) {
            return ActionStage.COMPLETED;
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
