package com.goosage.app.recovery.message.eligibility;

import com.goosage.app.recovery.message.domain.DomainResolutionResult;
import com.goosage.app.recovery.message.domain.DomainResolverShadow;
import com.goosage.app.recovery.message.domain.DomainType;
import com.goosage.app.recovery.message.subject.SubjectResolutionResult;
import com.goosage.app.recovery.message.subject.SubjectResolverShadow;
import com.goosage.app.recovery.message.subject.SubjectType;

public class StructuredEligibilityGateShadow {

    private final DomainResolverShadow domainResolver =
            new DomainResolverShadow();

    private final SubjectResolverShadow subjectResolver =
            new SubjectResolverShadow();


    public StructuredEligibilityResult resolve(
            String message
    ) {

        DomainResolutionResult domain =
                domainResolver.resolve(message);

        SubjectResolutionResult subject =
                subjectResolver.resolve(message);


        // ====================================================
        // 1. EXPLICIT NON-GAMBLING DOMAIN
        // ====================================================

        if (
                domain.domain()
                == DomainType.NON_GAMBLING
        ) {

            return new StructuredEligibilityResult(
                    EligibilityDecisionType.HOLD_NO_SUPPORTED_SIGNAL,
                    domain.domain(),
                    subject.subject(),
                    "NON_GAMBLING_DOMAIN"
            );
        }


        // ====================================================
        // 2. EXPLICIT THIRD-PARTY-ONLY SUBJECT
        //
        // UNKNOWN domain must not erase strong subject evidence.
        // ====================================================

        if (
                subject.subject()
                == SubjectType.THIRD_PARTY_ONLY
        ) {

            return new StructuredEligibilityResult(
                    EligibilityDecisionType.HOLD_THIRD_PARTY_CONTEXT,
                    domain.domain(),
                    subject.subject(),
                    "THIRD_PARTY_ONLY"
            );
        }


        // ====================================================
        // 3. UNKNOWN DOMAIN
        // ====================================================

        if (
                domain.domain()
                == DomainType.UNKNOWN
        ) {

            return new StructuredEligibilityResult(
                    EligibilityDecisionType.HOLD_NO_SUPPORTED_SIGNAL,
                    domain.domain(),
                    subject.subject(),
                    "DOMAIN_UNKNOWN"
            );
        }


        // ====================================================
        // 4. SUPPORTED GAMBLING + SELF ELIGIBILITY
        // ====================================================

        return new StructuredEligibilityResult(
                EligibilityDecisionType.ALLOW_CANONICAL,
                domain.domain(),
                subject.subject(),
                "SUPPORTED_DOMAIN_AND_SUBJECT"
        );
    }
}