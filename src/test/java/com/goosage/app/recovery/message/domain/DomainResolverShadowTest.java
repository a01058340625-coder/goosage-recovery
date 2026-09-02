package com.goosage.app.recovery.message.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DomainResolverShadowTest {

    private final DomainResolverShadow resolver =
            new DomainResolverShadow();


    @Test
    void explicitGamblingDomain() {
        DomainResolutionResult result =
                resolver.resolve(
                        "카지노 사이트에 들어가 로그인했습니다."
                );

        assertEquals(
                DomainType.GAMBLING,
                result.domain()
        );
        assertTrue(result.supported());
        assertEquals(
                DomainReason.EXPLICIT_GAMBLING_DOMAIN,
                result.reason()
        );
    }


    @Test
    void explicitNonGamblingGameMoneyDomain() {
        DomainResolutionResult result =
                resolver.resolve(
                        "온라인 게임머니 거래 사이트에서 결제수단을 등록했습니다."
                );

        assertEquals(
                DomainType.NON_GAMBLING,
                result.domain()
        );
        assertFalse(result.supported());
    }


    @Test
    void genericLoginPaymentRemainsUnknown() {
        DomainResolutionResult result =
                resolver.resolve(
                        "사이트에 들어가 로그인하고 금액을 입력했습니다."
                );

        assertEquals(
                DomainType.UNKNOWN,
                result.domain()
        );
        assertFalse(result.supported());
        assertEquals(
                DomainReason.INSUFFICIENT_DOMAIN_EVIDENCE,
                result.reason()
        );
    }


    @Test
    void lateCasinoConfirmation() {
        DomainResolutionResult result =
                resolver.resolve(
                        "링크를 눌러 화면에 들어갔는데 들어가 보니 카지노 사이트였습니다."
                );

        assertEquals(
                DomainType.GAMBLING,
                result.domain()
        );
        assertTrue(result.supported());
        assertTrue(result.lateConfirmation());
        assertEquals(
                DomainReason.LATE_DOMAIN_CONFIRMATION,
                result.reason()
        );
    }


    @Test
    void thirdPartyGamblingContextStillResolvesDomain() {
        DomainResolutionResult result =
                resolver.resolve(
                        "친구가 카지노 이야기를 했지만 저는 듣기만 했습니다."
                );

        assertEquals(
                DomainType.GAMBLING,
                result.domain()
        );
        assertTrue(result.supported());
    }


    @Test
    void genericTravelPaymentIsNonGambling() {
        DomainResolutionResult result =
                resolver.resolve(
                        "항공권을 예약하면서 카드로 여행 결제를 완료했습니다."
                );

        assertEquals(
                DomainType.NON_GAMBLING,
                result.domain()
        );
        assertFalse(result.supported());
    }

    @Test
    void explicitSingleBettingWordResolvesGamblingDomain() {
        DomainResolutionResult result =
                resolver.resolve(
                        "실제 베팅이 한 번 성립된 뒤 바로 계정을 다시 차단했어."
                );

        assertEquals(
                DomainType.GAMBLING,
                result.domain()
        );
        assertTrue(result.supported());
    }


}
