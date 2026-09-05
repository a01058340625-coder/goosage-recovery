package com.goosage.app.recovery.message.eligibility;

import com.goosage.app.recovery.message.domain.DomainType;
import com.goosage.app.recovery.message.subject.SubjectType;

public record StructuredEligibilityResult(
        EligibilityDecisionType decision,
        DomainType domain,
        SubjectType subject,
        String reason
) {
}