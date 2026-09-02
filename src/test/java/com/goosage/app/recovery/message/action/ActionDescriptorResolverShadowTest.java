package com.goosage.app.recovery.message.action;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ActionDescriptorResolverShadowTest {

    private final ActionDescriptorResolverShadow resolver =
            new ActionDescriptorResolverShadow();

    @Test
    void searchInput() {
        ActionDescriptor result =
                resolver.resolve(
                        "검색창에 사이트 이름을 입력했다."
                );

        assertEquals(
                ActionType.SEARCH,
                result.actionType()
        );

        assertEquals(
                ActionStage.INPUT,
                result.actionStage()
        );

        assertFalse(result.completed());
    }

    @Test
    void loginCompleted() {
        ActionDescriptor result =
                resolver.resolve(
                        "비밀번호를 입력하고 로그인에 성공했다."
                );

        assertEquals(
                ActionType.LOGIN,
                result.actionType()
        );

        assertEquals(
                ActionStage.COMPLETED,
                result.actionStage()
        );

        assertTrue(result.completed());
    }

    @Test
    void fundingInput() {
        ActionDescriptor result =
                resolver.resolve(
                        "입금 화면에서 금액을 입력했다."
                );

        assertEquals(
                ActionType.FUNDING,
                result.actionType()
        );

        assertEquals(
                ActionStage.INPUT,
                result.actionStage()
        );
    }

    @Test
    void wagerCompleted() {
        ActionDescriptor result =
                resolver.resolve(
                        "베팅을 실행했고 정상적으로 성립됐다."
                );

        assertEquals(
                ActionType.WAGER,
                result.actionType()
        );

        assertEquals(
                ActionStage.COMPLETED,
                result.actionStage()
        );
    }

    @Test
    void recoveryStarted() {
        ActionDescriptor result =
                resolver.resolve(
                        "상담센터를 찾아봤다."
                );

        assertEquals(
                ActionType.RECOVERY,
                result.actionType()
        );

        assertEquals(
                ActionStage.STARTED,
                result.actionStage()
        );
    }

    @Test
    void accountControlSubmitted() {
        ActionDescriptor result =
                resolver.resolve(
                        "차단 해제 요청 버튼을 눌렀다."
                );

        assertEquals(
                ActionType.ACCOUNT_CONTROL,
                result.actionType()
        );

        assertEquals(
                ActionStage.SUBMITTED,
                result.actionStage()
        );
    }

    @Test
    void unknownAction() {
        ActionDescriptor result =
                resolver.resolve(
                        "오늘 기분이 좀 이상했다."
                );

        assertEquals(
                ActionType.UNKNOWN,
                result.actionType()
        );

        assertEquals(
                ActionStage.UNKNOWN,
                result.actionStage()
        );
    }
}
