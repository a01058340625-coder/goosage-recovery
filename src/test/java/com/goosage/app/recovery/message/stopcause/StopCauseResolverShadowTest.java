package com.goosage.app.recovery.message.stopcause;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StopCauseResolverShadowTest {

    private final StopCauseResolverShadow resolver =
            new StopCauseResolverShadow();

    @Test
    void detectsSelfStop() {
        assertEquals(
                StopCauseType.SELF_STOP,
                resolver.resolve(
                        "\uc785\uae08 \ud654\uba74\uae4c\uc9c0 \uac14\uc9c0\ub9cc \uadf8\ub0e5 \ucc3d\uc744 \ub2eb\uc558\uc5b4."
                ).stopCause()
        );
    }

    @Test
    void detectsTechnicalFailure() {
        assertEquals(
                StopCauseType.TECHNICAL_FAILURE,
                resolver.resolve(
                        "\uacb0\uc81c \ubc84\ud2bc\uc744 \ub20c\ub800\ub294\ub370 \uc740\ud589 \uc778\uc99d \ub2e8\uacc4\uc5d0\uc11c \uc624\ub958\uac00 \ub0ac\uc5b4."
                ).stopCause()
        );
    }

    @Test
    void detectsExternalInterruption() {
        assertEquals(
                StopCauseType.EXTERNAL_INTERRUPTION,
                resolver.resolve(
                        "\uae08\uc561\uc744 \uc785\ub825\ud558\ub824\ub294\ub370 \uce5c\uad6c\ud55c\ud14c \uc804\ud654\uac00 \uc654\uc5b4."
                ).stopCause()
        );
    }

    @Test
    void detectsCompletion() {
        assertEquals(
                StopCauseType.COMPLETION,
                resolver.resolve(
                        "\ub85c\uadf8\uc778\uc5d0 \uc131\uacf5\ud588\uace0 \ucc98\ub9ac\uac00 \uc644\ub8cc\ub410\uc5b4."
                ).stopCause()
        );
    }

    @Test
    void unknownWithoutStopEvidence() {
        assertEquals(
                StopCauseType.UNKNOWN,
                resolver.resolve(
                        "\uc624\ub298 \uacbd\uae30 \uacb0\uacfc\ub97c \uc7a0\uae50 \ubd24\uc5b4."
                ).stopCause()
        );
    }

    @Test
    void eventSelfStop() {
        assertEquals(
                StopCauseType.SELF_STOP,
                resolver.resolve(
                        new StopCauseEventInput(
                                "\uc785\uae08 \ud654\uba74\uc5d0 \ub4e4\uc5b4\uac14\ub2e4.",
                                "\uae08\uc561\uc744 \uc785\ub825\ud558\ub824\uace0 \ud588\ub2e4.",
                                "\ub9c8\uc74c\uc744 \ubc14\uafd4 \ucc3d\uc744 \ub2eb\uc558\ub2e4."
                        )
                ).stopCause()
        );
    }

    @Test
    void eventTechnicalFailure() {
        assertEquals(
                StopCauseType.TECHNICAL_FAILURE,
                resolver.resolve(
                        new StopCauseEventInput(
                                "\uacb0\uc81c \ubc84\ud2bc\uc744 \ub20c\ub800\ub2e4.",
                                "\uc785\uae08\uc744 \uc2dc\ub3c4\ud588\ub2e4.",
                                "\uc740\ud589 \uc778\uc99d \ub2e8\uacc4\uc5d0\uc11c \uc624\ub958\uac00 \ub0ac\ub2e4."
                        )
                ).stopCause()
        );
    }

    @Test
    void eventExternalInterruption() {
        assertEquals(
                StopCauseType.EXTERNAL_INTERRUPTION,
                resolver.resolve(
                        new StopCauseEventInput(
                                "\uae08\uc561 \uc785\ub825 \ud654\uba74\uc744 \ubcf4\uace0 \uc788\uc5c8\ub2e4.",
                                "\uc785\uae08\uc744 \uc900\ube44\ud588\ub2e4.",
                                "\uce5c\uad6c\ud55c\ud14c \uc804\ud654\uac00 \uc654\ub2e4."
                        )
                ).stopCause()
        );
    }

    @Test
    void eventExternalDistraction() {
        assertEquals(
                StopCauseType.EXTERNAL_DISTRACTION,
                resolver.resolve(
                        new StopCauseEventInput(
                                "\uc0ac\uc774\ud2b8\ub97c \ubcf4\uace0 \uc788\uc5c8\ub2e4.",
                                "\ub354 \ubcf4\ub824\uace0 \ud588\ub2e4.",
                                "\ub2e4\ub978 \uc77c\uc744 \ud588\ub2e4."
                        )
                ).stopCause()
        );
    }

    @Test
    void eventNaturalNoFurtherAction() {
        assertEquals(
                StopCauseType.NATURAL_NO_FURTHER_ACTION,
                resolver.resolve(
                        new StopCauseEventInput(
                                "\ub85c\uadf8\uc778 \ud654\uba74\uc744 \ubcf4\uc558\ub2e4.",
                                "\ucd94\uac00 \ud589\ub3d9\uc740 \uc5c6\uc5c8\ub2e4.",
                                "\uadf8 \ub4a4\ub85c\ub294 \ub2e4\uc2dc \uc811\uc18d\ud558\uc9c0 \uc54a\uc558\ub2e4."
                        )
                ).stopCause()
        );
    }

    @Test
    void eventCompletion() {
        assertEquals(
                StopCauseType.COMPLETION,
                resolver.resolve(
                        new StopCauseEventInput(
                                "\ub85c\uadf8\uc778\uc744 \uc2dc\ub3c4\ud588\ub2e4.",
                                "\ube44\ubc00\ubc88\ud638\ub97c \uc785\ub825\ud588\ub2e4.",
                                "\ub85c\uadf8\uc778\uc5d0 \uc131\uacf5\ud588\ub2e4."
                        )
                ).stopCause()
        );
    }


}
