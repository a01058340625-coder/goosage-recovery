package com.goosage.app.recovery.message.segmentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EventSegmenterShadowTest {

    private final EventSegmenterShadow segmenter =
            new EventSegmenterShadow();

    @Test
    void splitsSentenceBoundary() {
        var result = segmenter.segment(
                "\ub85c\uadf8\uc778\ud588\ub2e4. \uc785\uae08 \ud654\uba74\uc73c\ub85c \ub4e4\uc5b4\uac14\ub2e4."
        );

        assertEquals(2, result.size());
    }

    @Test
    void splitsNextDayTransition() {
        var result = segmenter.segment(
                "\uc5b4\uc81c \ubca0\ud305\uc744 \ud588\ub2e4. \ub2e4\uc74c \ub0a0 \uc571\uc744 \uc0ad\uc81c\ud588\ub2e4."
        );

        assertEquals(2, result.size());
    }

    @Test
    void splitsButKeepsOrder() {
        var result = segmenter.segment(
                "\uc0ac\uc774\ud2b8\uc5d0 \ub85c\uadf8\uc778\ud588\ub2e4. \uadf8\ub7f0\ub370 \uc785\uae08\uc740 \ud558\uc9c0 \uc54a\uc558\ub2e4."
        );

        assertEquals(2, result.size());
        assertEquals(0, result.get(0).index());
        assertEquals(1, result.get(1).index());
    }

    @Test
    void singleEventRemainsSingle() {
        var result = segmenter.segment(
                "\uac80\uc0c9\ucc3d\uc5d0 \uc0ac\uc774\ud2b8 \uc774\ub984\uc744 \uc785\ub825\ud588\ub2e4."
        );

        assertEquals(1, result.size());
    }

    @Test
    void blankProducesNoEvents() {
        assertEquals(
                0,
                segmenter.segment("").size()
        );
    }
}
