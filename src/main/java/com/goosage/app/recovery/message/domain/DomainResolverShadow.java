package com.goosage.app.recovery.message.domain;

import java.util.ArrayList;
import java.util.List;

public class DomainResolverShadow {

    public DomainResolutionResult resolve(String message) {
        String normalized = normalize(message);

        if (normalized.isBlank()) {
            return unknown(normalized);
        }

        List<String> nonGamblingEvidence =
                findEvidence(
                        normalized,
                        "온라인 게임머니 거래",
                        "게임머니 거래",
                        "게임 머니 거래",
                        "항공권",
                        "여행 결제",
                        "OTT 결제",
                        "쇼핑 결제"
                );

        List<String> gamblingActionEvidence =
                findEvidence(
                        normalized,
                        "돈을 걸었",
                        "돈을 걸어",
                        "돈을 걸기",
                        "한 번 걸었",
                        "한 번 걸어",
                        "실제로 돈을 넣고 한 번 걸"
                );

        List<String> gamblingEvidence =
                findEvidence(
                        normalized,
                        "도박",
                        "카지노",
                        "슬롯",
                        "스포츠베팅",
                        "스포츠 베팅",
                        "베팅",
                        "배팅",
                        "베팅 사이트",
                        "도박 앱",
                        "카지노 앱",
                        "바카라",
                        "룰렛",
                        "경마",
                        "토토"
                );

        boolean sportsOddsComposite =
                (
                        normalized.contains("스포츠")
                        || normalized.contains("경기")
                        || normalized.contains("축구")
                )
                && normalized.contains("배당");

        boolean sportsWagerComposite =
                (
                        normalized.contains("스포츠")
                        || normalized.contains("경기")
                )
                && (
                        (
                                normalized.contains("돈을")
                                && normalized.contains("걸")
                        )
                        || normalized.contains("한 번 걸")
                );

        boolean sportsFundingComposite =
                (
                        normalized.contains("스포츠")
                        || normalized.contains("경기")
                )
                && (
                        normalized.contains("입금")
                        || normalized.contains("잔액")
                        || normalized.contains("결제수단")
                );

        boolean hasComposite =
                sportsOddsComposite
                || sportsWagerComposite
                || sportsFundingComposite;

        boolean hasAnyGamblingEvidence =
                !gamblingEvidence.isEmpty()
                || !gamblingActionEvidence.isEmpty()
                || hasComposite;

        // Conflicting explicit domains stay unresolved.
        if (
                !nonGamblingEvidence.isEmpty()
                && hasAnyGamblingEvidence
        ) {
            return new DomainResolutionResult(
                    DomainType.UNKNOWN,
                    false,
                    0.50,
                    DomainReason.CONFLICTING_DOMAIN_EVIDENCE,
                    DomainEvidenceSource.UNKNOWN_CONTEXT,
                    merge(
                            nonGamblingEvidence,
                            gamblingEvidence
                    ),
                    false
            );
        }

        // 1. Explicit gambling noun / late confirmation.
        if (!gamblingEvidence.isEmpty()) {
            boolean lateConfirmation =
                    looksLikeLateDomainConfirmation(
                            normalized
                    );

            return new DomainResolutionResult(
                    DomainType.GAMBLING,
                    true,
                    lateConfirmation ? 0.90 : 0.95,
                    lateConfirmation
                            ? DomainReason.LATE_DOMAIN_CONFIRMATION
                            : DomainReason.EXPLICIT_GAMBLING_DOMAIN,
                    DomainEvidenceSource.EXPLICIT_GAMBLING_NOUN,
                    List.copyOf(gamblingEvidence),
                    lateConfirmation
            );
        }

        // 2. Explicit gambling action.
        if (!gamblingActionEvidence.isEmpty()) {
            return new DomainResolutionResult(
                    DomainType.GAMBLING,
                    true,
                    0.95,
                    DomainReason.EXPLICIT_GAMBLING_DOMAIN,
                    DomainEvidenceSource.EXPLICIT_GAMBLING_ACTION,
                    List.copyOf(gamblingActionEvidence),
                    false
            );
        }

        // 3. Composite gambling evidence.
        if (sportsWagerComposite || sportsFundingComposite) {
            return new DomainResolutionResult(
                    DomainType.GAMBLING,
                    true,
                    0.90,
                    DomainReason.EXPLICIT_GAMBLING_DOMAIN,
                    DomainEvidenceSource.COMPOSITE_GAMBLING_EVIDENCE,
                    List.of("스포츠/경기+웨이저/펀딩"),
                    false
            );
        }

        if (sportsOddsComposite) {
            return new DomainResolutionResult(
                    DomainType.GAMBLING,
                    true,
                    0.90,
                    DomainReason.EXPLICIT_GAMBLING_DOMAIN,
                    DomainEvidenceSource.COMPOSITE_GAMBLING_EVIDENCE,
                    List.of("스포츠/경기+배당"),
                    false
            );
        }

        // 4. Explicit non-gambling domain.
        if (!nonGamblingEvidence.isEmpty()) {
            return new DomainResolutionResult(
                    DomainType.NON_GAMBLING,
                    false,
                    0.95,
                    DomainReason.EXPLICIT_NON_GAMBLING_DOMAIN,
                    DomainEvidenceSource.GENERIC_ACTION_CONTEXT,
                    List.copyOf(nonGamblingEvidence),
                    false
            );
        }

        return unknown(normalized);
    }


    private DomainResolutionResult unknown(String normalized) {
        List<String> historicalEvidence =
                findEvidence(
                        normalized,
                        "예전에 하던",
                        "예전에 쓰던",
                        "예전에 보던",
                        "예전에 자주 하던",
                        "예전에 같이 하던",
                        "예전에 잃었던",
                        "본전"
                );

        if (!historicalEvidence.isEmpty()) {
            return new DomainResolutionResult(
                    DomainType.UNKNOWN,
                    false,
                    0.40,
                    DomainReason.INSUFFICIENT_DOMAIN_EVIDENCE,
                    DomainEvidenceSource.HISTORICAL_DOMAIN_REFERENCE,
                    List.copyOf(historicalEvidence),
                    false
            );
        }

        List<String> recoveryContextEvidence =
                findEvidence(
                        normalized,
                        "계정을 막",
                        "계정 차단",
                        "차단 해제",
                        "해제 요청",
                        "고객센터",
                        "상담센터",
                        "앱을 지우",
                        "앱을 삭제"
                );

        if (!recoveryContextEvidence.isEmpty()) {
            return new DomainResolutionResult(
                    DomainType.UNKNOWN,
                    false,
                    0.35,
                    DomainReason.INSUFFICIENT_DOMAIN_EVIDENCE,
                    DomainEvidenceSource.RECOVERY_CONTEXT_REFERENCE,
                    List.copyOf(recoveryContextEvidence),
                    false
            );
        }

        List<String> genericActionEvidence =
                findEvidence(
                        normalized,
                        "로그인 화면",
                        "입금 화면",
                        "입금 버튼",
                        "사이트 이름",
                        "사이트 주소",
                        "관련 사이트",
                        "관련 화면"
                );

        if (!genericActionEvidence.isEmpty()) {
            return new DomainResolutionResult(
                    DomainType.UNKNOWN,
                    false,
                    0.25,
                    DomainReason.INSUFFICIENT_DOMAIN_EVIDENCE,
                    DomainEvidenceSource.GENERIC_ACTION_CONTEXT,
                    List.copyOf(genericActionEvidence),
                    false
            );
        }

        return new DomainResolutionResult(
                DomainType.UNKNOWN,
                false,
                0.20,
                DomainReason.INSUFFICIENT_DOMAIN_EVIDENCE,
                DomainEvidenceSource.UNKNOWN_CONTEXT,
                List.of(),
                false
        );
    }


    private boolean looksLikeLateDomainConfirmation(
            String text
    ) {
        return containsAny(
                text,
                "카지노 사이트인 건 들어가고 나서 알았",
                "카지노 사이트인 것은 들어가고 나서 알았",
                "들어가 보니 카지노",
                "들어가보니 카지노",
                "알고 보니 카지노",
                "알고보니 카지노",
                "보니 카지노 사이트",
                "카지노 사이트였습니다"
        );
    }


    private List<String> findEvidence(
            String text,
            String... candidates
    ) {
        List<String> evidence = new ArrayList<>();

        for (String candidate : candidates) {
            if (
                    text.contains(candidate)
                    && !evidence.contains(candidate)
            ) {
                evidence.add(candidate);
            }
        }

        return evidence;
    }


    private List<String> merge(
            List<String> first,
            List<String> second
    ) {
        List<String> merged =
                new ArrayList<>(first);

        for (String value : second) {
            if (!merged.contains(value)) {
                merged.add(value);
            }
        }

        return List.copyOf(merged);
    }


    private String normalize(String message) {
        if (message == null) {
            return "";
        }

        return message
                .trim()
                .replaceAll("\\s+", " ");
    }


    private boolean containsAny(
            String text,
            String... candidates
    ) {
        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                return true;
            }
        }

        return false;
    }
}
