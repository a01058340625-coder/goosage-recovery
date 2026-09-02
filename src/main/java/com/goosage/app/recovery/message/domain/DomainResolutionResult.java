package com.goosage.app.recovery.message.domain;

import java.util.List;

public record DomainResolutionResult(
        DomainType domain,
        boolean supported,
        double confidence,
        DomainReason reason,
        DomainEvidenceSource evidenceSource,
        List<String> evidence,
        boolean lateConfirmation
) {
}
