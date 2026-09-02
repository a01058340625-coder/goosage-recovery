package com.goosage.app.recovery.message.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.stopcause.StopCauseType;

class EventDescriptorResolverShadowTest {

    private final EventDescriptorResolverShadow resolver =
            new EventDescriptorResolverShadow();

    @Test
    void searchInputEvent() {
        EventDescriptor result =
                resolver.resolve(
                        "\uac80\uc0c9\ucc3d\uc5d0 \uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uc785\ub825\ud588\ub2e4."
                );

        assertEquals(EventType.SEARCH_INPUT, result.eventType());
        assertEquals(ActionType.SEARCH, result.actionType());
        assertEquals(ActionStage.INPUT, result.actionStage());
        assertFalse(result.completed());
    }

    @Test
    void loginCompletedEvent() {
        EventDescriptor result =
                resolver.resolve(
                        "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud558\uace0 \ub85c\uadf8\uc778\uc5d0 \uc131\uacf5\ud588\ub2e4."
                );

        assertEquals(EventType.LOGIN, result.eventType());
        assertTrue(result.completed());
        assertEquals(StopCauseType.COMPLETION, result.stopCause());
    }

    @Test
    void fundingSelfStopEvent() {
        EventDescriptor result =
                resolver.resolve(
                        "\uc785\uae08 \ud654\uba74\uc5d0\uc11c \uae08\uc561\uc744 \uc785\ub825\ud558\ub2e4\uac00 \ub9c8\uc74c\uc744 \ubc14\uafd4 \ucc3d\uc744 \ub2eb\uc558\ub2e4."
                );

        assertEquals(EventType.FUNDING, result.eventType());
        assertEquals(ActionStage.INPUT, result.actionStage());
        assertEquals(StopCauseType.SELF_STOP, result.stopCause());
    }

    @Test
    void wagerTechnicalFailureEvent() {
        EventDescriptor result =
                resolver.resolve(
                        "\ubca0\ud305 \ubc84\ud2bc\uc744 \ub20c\ub800\uc9c0\ub9cc \uc8fc\ubb38\uc774 \uc2e4\ud328\ud588\ub2e4."
                );

        assertEquals(EventType.WAGER, result.eventType());
        assertEquals(ActionStage.SUBMITTED, result.actionStage());
        assertEquals(StopCauseType.TECHNICAL_FAILURE, result.stopCause());
    }

    @Test
    void recoveryEvent() {
        EventDescriptor result =
                resolver.resolve(
                        "\uc0c1\ub2f4\uc13c\ud130\ub97c \ucc3e\uc544\ubd24\ub2e4."
                );

        assertEquals(EventType.RECOVERY_ACTION, result.eventType());
    }

    @Test
    void accountControlEvent() {
        EventDescriptor result =
                resolver.resolve(
                        "\ucc28\ub2e8 \ud574\uc81c \uc694\uccad \ubc84\ud2bc\uc744 \ub20c\ub800\ub2e4."
                );

        assertEquals(EventType.ACCOUNT_CONTROL, result.eventType());
    }

    @Test
    void unknownEvent() {
        EventDescriptor result =
                resolver.resolve(
                        "\uc624\ub298 \uae30\ubd84\uc774 \uc870\uae08 \uc774\uc0c1\ud588\ub2e4."
                );

        assertEquals(EventType.UNKNOWN, result.eventType());
    }
}
