package com.goosage.app.recovery.message.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DomainResolverShadowMappingTest {

    private final DomainResolverShadow resolver =
            new DomainResolverShadow();


    @Test
    void validation413DomainEvidenceAbsent() {
        DomainResolutionResult result =
                resolver.resolve(
                        "검색창을 열고 사이트 이름을 찾아봤지만 "
                        + "어떤 종류의 사이트인지는 문장에 나오지 않습니다."
                );

        assertEquals(
                DomainType.UNKNOWN,
                result.domain()
        );
        assertFalse(result.supported());
    }


    @Test
    void validation448ThirdPartyCasinoToSelfAction() {
        DomainResolutionResult result =
                resolver.resolve(
                        "친구가 카지노 이야기를 해서 검색해봤고 "
                        + "사이트에 접속해 로그인한 뒤 슬롯을 몇 번 돌렸습니다."
                );

        assertEquals(
                DomainType.GAMBLING,
                result.domain()
        );
        assertTrue(result.supported());
    }


    @Test
    void validation491ThirdPartySportsBettingFlow() {
        DomainResolutionResult result =
                resolver.resolve(
                        "친구가 알려준 사이트에 들어가 회원가입 후 로그인하고 "
                        + "입금을 완료한 뒤 스포츠 베팅을 두 번 했습니다."
                );

        assertEquals(
                DomainType.GAMBLING,
                result.domain()
        );
        assertTrue(result.supported());
    }


    @Test
    void validation494GameMoneyIsNonGambling() {
        DomainResolutionResult result =
                resolver.resolve(
                        "회사 동료가 예전에 하던 온라인 게임머니 거래 이야기를 하길래 "
                        + "관련 사이트를 검색하고 로그인한 뒤 결제 수단을 등록했습니다."
                );

        assertEquals(
                DomainType.NON_GAMBLING,
                result.domain()
        );
        assertFalse(result.supported());
    }


    @Test
    void validation497TravelPaymentIsNonGambling() {
        DomainResolutionResult result =
                resolver.resolve(
                        "여행 예약을 위해 항공권 결제 페이지에 들어가 "
                        + "로그인하고 카드로 결제를 완료했습니다."
                );

        assertEquals(
                DomainType.NON_GAMBLING,
                result.domain()
        );
        assertFalse(result.supported());
    }


    @Test
    void validation499SportsBettingIsGambling() {
        DomainResolutionResult result =
                resolver.resolve(
                        "오랜만에 스포츠 베팅을 하려고 사이트에 접속해서 로그인하고 "
                        + "소액을 입금한 뒤 두 번 베팅했습니다."
                );

        assertEquals(
                DomainType.GAMBLING,
                result.domain()
        );
        assertTrue(result.supported());
    }
}