package com.goosage.app.recovery.message.signalshadow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.goosage.app.recovery.message.action.ActionStage;
import com.goosage.app.recovery.message.action.ActionType;
import com.goosage.app.recovery.message.event.EventDescriptor;
import com.goosage.app.recovery.message.event.EventType;
import com.goosage.app.recovery.message.stopcause.StopCauseType;

class EventSignalMapperShadowTest {

    private final EventSignalMapperShadow mapper =
            new EventSignalMapperShadow();

    @Test
    void searchInputIsAttempt() {
        ShadowSignalVector result =
                mapper.map(
                        new EventDescriptor(
                                EventType.SEARCH_INPUT,
                                ActionType.SEARCH,
                                ActionStage.INPUT,
                                false,
                                StopCauseType.UNKNOWN
                        )
                );

        assertEquals(
                new ShadowSignalVector(
                        0, 1, 0, 0, 0
                ),
                result
        );
    }

    @Test
    void selfStoppedAttemptIsBlocked() {
        ShadowSignalVector result =
                mapper.map(
                        new EventDescriptor(
                                EventType.FUNDING,
                                ActionType.FUNDING,
                                ActionStage.INPUT,
                                false,
                                StopCauseType.SELF_STOP
                        )
                );

        assertEquals(
                new ShadowSignalVector(
                        0, 1, 1, 0, 0
                ),
                result
        );
    }

    @Test
    void completedWagerIsRelapse() {
        ShadowSignalVector result =
                mapper.map(
                        new EventDescriptor(
                                EventType.WAGER,
                                ActionType.WAGER,
                                ActionStage.COMPLETED,
                                true,
                                StopCauseType.COMPLETION
                        )
                );

        assertEquals(
                new ShadowSignalVector(
                        0, 1, 0, 0, 1
                ),
                result
        );
    }

    @Test
    void completedRecoveryIsRecovery() {
        ShadowSignalVector result =
                mapper.map(
                        new EventDescriptor(
                                EventType.RECOVERY_ACTION,
                                ActionType.RECOVERY,
                                ActionStage.COMPLETED,
                                true,
                                StopCauseType.COMPLETION
                        )
                );

        assertEquals(
                new ShadowSignalVector(
                        0, 0, 0, 1, 0
                ),
                result
        );
    }

    @Test
    void aggregatesMultipleEvents() {
        ShadowSignalVector result =
                mapper.mapAll(
                        List.of(
                                new EventDescriptor(
                                        EventType.SEARCH_INPUT,
                                        ActionType.SEARCH,
                                        ActionStage.INPUT,
                                        false,
                                        StopCauseType.UNKNOWN
                                ),
                                new EventDescriptor(
                                        EventType.FUNDING,
                                        ActionType.FUNDING,
                                        ActionStage.INPUT,
                                        false,
                                        StopCauseType.SELF_STOP
                                ),
                                new EventDescriptor(
                                        EventType.WAGER,
                                        ActionType.WAGER,
                                        ActionStage.COMPLETED,
                                        true,
                                        StopCauseType.COMPLETION
                                ),
                                new EventDescriptor(
                                        EventType.RECOVERY_ACTION,
                                        ActionType.RECOVERY,
                                        ActionStage.COMPLETED,
                                        true,
                                        StopCauseType.COMPLETION
                                )
                        )
                );

        assertEquals(
                new ShadowSignalVector(
                        0, 1, 1, 1, 1
                ),
                result
        );
    }

    @Test
    void mergesUrgeIntoEventSignals() {
        ShadowSignalVector result =
                mapper.mergeUrge(
                        new ShadowSignalVector(
                                0, 1, 1, 1, 1
                        ),
                        1
                );

        assertEquals(
                new ShadowSignalVector(
                        1, 1, 1, 1, 1
                ),
                result
        );
    }
}
