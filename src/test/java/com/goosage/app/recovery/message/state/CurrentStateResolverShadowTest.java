package com.goosage.app.recovery.message.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.domain.DomainType;
import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;
import com.goosage.app.recovery.message.stopcause.StopCauseType;

class CurrentStateResolverShadowTest {

    private final CurrentStateResolverShadow resolver =
            new CurrentStateResolverShadow();

    @Test
    void searchInputState() {
        CurrentStateShadow state =
                resolver.resolve(
                        "\ub3c4\ubc15 \uc0ac\uc774\ud2b8\ub97c \uac80\uc0c9\ud558\uace0 \uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uc785\ub825\ud588\ub2e4."
                );

        assertEquals(DomainType.GAMBLING, state.domain());
        assertEquals(ActionType.SEARCH, state.actionType());
        assertEquals(ActionStage.INPUT, state.actionStage());

        assertEquals(
                new ShadowSignalVector(
                        0, 1, 0, 0, 0
                ),
                state.signalVector()
        );
    }

    @Test
    void selfStoppedFundingState() {
        CurrentStateShadow state =
                resolver.resolve(
                        "\ub3c4\ubc15 \uc0ac\uc774\ud2b8 \uc785\uae08 \ud654\uba74\uc5d0\uc11c \uae08\uc561\uc744 \uc785\ub825\ud558\ub2e4\uac00 \ub9c8\uc74c\uc744 \ubc14\uafd4 \ucc3d\uc744 \ub2eb\uc558\ub2e4."
                );

        assertEquals(ActionType.FUNDING, state.actionType());
        assertEquals(ActionStage.INPUT, state.actionStage());
        assertEquals(StopCauseType.SELF_STOP, state.stopCause());

        assertEquals(
                new ShadowSignalVector(
                        0, 1, 1, 0, 0
                ),
                state.signalVector()
        );
    }

    @Test
    void completedWagerWithUrgeState() {
        CurrentStateShadow state =
                resolver.resolve(
                        "\ub3c4\ubc15 \ubca0\ud305\uc744 \uc2e4\ud589\ud588\uace0 \uc131\ub9bd\ub410\ub2e4. \uadf8 \ub4a4 \ub2e4\uc2dc \ud574\ubcfc\uae4c \ud558\ub294 \uc0dd\uac01\uc774 \ub4e4\uc5c8\ub2e4."
                );

        assertEquals(ActionType.WAGER, state.actionType());
        assertEquals(ActionStage.COMPLETED, state.actionStage());

        assertEquals(
                new ShadowSignalVector(
                        1, 1, 0, 0, 1
                ),
                state.signalVector()
        );
    }

    @Test
    void unknownState() {
        CurrentStateShadow state =
                resolver.resolve(
                        "\uc624\ub298 \uae30\ubd84\uc774 \uc870\uae08 \uc774\uc0c1\ud588\ub2e4."
                );

        assertEquals(DomainType.UNKNOWN, state.domain());
        assertEquals(ActionType.UNKNOWN, state.actionType());

        assertEquals(
                new ShadowSignalVector(
                        0, 0, 0, 0, 0
                ),
                state.signalVector()
        );
    }
}
