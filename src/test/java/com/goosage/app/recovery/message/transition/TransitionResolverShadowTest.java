package com.goosage.app.recovery.message.transition;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.event.EventType;
import com.goosage.app.recovery.message.signalshadow.ShadowSignalVector;

class TransitionResolverShadowTest {

    private final TransitionResolverShadow resolver =
            new TransitionResolverShadow();

    @Test
    void searchToAccessTransition() {
        TransitionShadow result =
                resolver.resolve(
                        "\ub3c4\ubc15 \uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uac80\uc0c9\ucc3d\uc5d0 \uc785\ub825\ud588\ub2e4.",
                        "\ub9c1\ud06c\ub97c \ub20c\ub7ec\ubd24\uace0 \uc0ac\uc774\ud2b8 \ud654\uba74\uc774 \uc5f4\ub838\ub2e4.",
                        "\ub3c4\ubc15 \uc0ac\uc774\ud2b8\uc5d0 \uc811\uc18d\ud588\ub2e4."
                );

        assertEquals(
                ActionType.SEARCH,
                result.beforeState().actionType()
        );

        assertEquals(
                EventType.SITE_ACCESS,
                result.event().eventType()
        );

        assertEquals(
                ActionType.ACCESS,
                result.afterState().actionType()
        );
    }

    @Test
    void fundingToBlockedTransition() {
        TransitionShadow result =
                resolver.resolve(
                        "\ub3c4\ubc15 \uc0ac\uc774\ud2b8 \uc785\uae08 \ud654\uba74\uc5d0 \ub4e4\uc5b4\uac14\ub2e4.",
                        "\uae08\uc561\uc744 \uc785\ub825\ud558\ub824\ub2e4\uac00 \ub9c8\uc74c\uc744 \ubc14\uafd4 \ucc3d\uc744 \ub2eb\uc558\ub2e4.",
                        "\ub3c4\ubc15 \uc0ac\uc774\ud2b8 \uc785\uae08 \uc2dc\ub3c4\ub97c \uba48\ucdc4\uace0 \ucc3d\uc744 \ub2eb\uc558\ub2e4."
                );

        assertEquals(
                new ShadowSignalVector(
                        0, 1, 1, 0, 0
                ),
                result.afterState().signalVector()
        );
    }
}
