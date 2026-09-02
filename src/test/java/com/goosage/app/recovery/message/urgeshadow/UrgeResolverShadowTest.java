package com.goosage.app.recovery.message.urgeshadow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UrgeResolverShadowTest {

    private final UrgeResolverShadow resolver =
            new UrgeResolverShadow();

    @Test
    void detectsUrge() {
        assertEquals(
                1,
                resolver.resolve(
                        "오늘 다시 해볼까 하는 생각이 계속 났다."
                ).urge()
        );
    }

    @Test
    void detectsLossRecoveryUrge() {
        assertEquals(
                1,
                resolver.resolve(
                        "예전에 잃은 돈 때문에 본전 생각이 났다."
                ).urge()
        );
    }

    @Test
    void currentNegationSuppressesUrge() {
        assertEquals(
                0,
                resolver.resolve(
                        "오늘은 다시 시도하고 싶은 생각도 없다."
                ).urge()
        );
    }

    @Test
    void curiosityIsNotUrge() {
        assertEquals(
                0,
                resolver.resolve(
                        "그냥 궁금해서 사이트 화면을 봤다."
                ).urge()
        );
    }
}
