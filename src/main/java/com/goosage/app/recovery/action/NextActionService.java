package com.goosage.app.recovery.action;

import org.springframework.stereotype.Service;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.recovery.RecoverySnapshot;

@Service
public class NextActionService {

    public NextActionType decide(RecoverySnapshot snap, PredictionReasonCode reasonCode) {

        if (reasonCode == null) {
            return NextActionType.RECOVERY_CHECK;
        }

        if (reasonCode == PredictionReasonCode.TODAY_DONE) {
            return NextActionType.TODAY_SAFE;
        }

        if (reasonCode == PredictionReasonCode.DATA_POOR) {
            return NextActionType.MINIMUM_CONTACT;
        }

        if (reasonCode == PredictionReasonCode.MINIMUM_ACTION) {
            return NextActionType.MINIMUM_CONTACT;
        }

        if (reasonCode == PredictionReasonCode.REVIEW_WRONG_PENDING) {
            return NextActionType.PROCESS_RISK_SIGNAL;
        }

        if (reasonCode == PredictionReasonCode.WRONG_HEAVY) {
            return NextActionType.DO_RECOVERY_ACTION;
        }

        if (reasonCode == PredictionReasonCode.URGE_HIGH) {
            return NextActionType.PROCESS_RISK_SIGNAL;
        }

        if (reasonCode == PredictionReasonCode.RELAPSE_RISK) {
            return NextActionType.PROCESS_RISK_SIGNAL;
        }

        if (reasonCode == PredictionReasonCode.RECOVERY_PROGRESS) {

            if (snap != null && snap.state() != null) {
                int relapse = snap.state().relapseSignalCount();
                int recovery = snap.state().recoveryActionCount();
                int blocked = snap.state().betBlockedCount();
                int attempts = snap.state().betAttempts();
                int urge = snap.state().urgeLogs();

                // 회복/방어 신호가 있고, 직접 재발 위험이 없으면 회복 행동 유지
                if (relapse == 0 && attempts == 0 && urge == 0 && (recovery > 0 || blocked > 0)) {
                    return NextActionType.DO_RECOVERY_ACTION;
                }

                // 회복은 있으나 아직 불안정하면 체크
                if (relapse == 0 && (recovery > 0 || blocked > 0)) {
                    return NextActionType.RECOVERY_CHECK;
                }

                // 재발 신호가 섞였으면 위험 처리
                if (relapse > 0 || attempts > 0) {
                    return NextActionType.PROCESS_RISK_SIGNAL;
                }

                return NextActionType.RECOVERY_CHECK;
            }

            return NextActionType.RECOVERY_CHECK;
        }

        if (reasonCode == PredictionReasonCode.RECOVERY_SAFE) {
            return NextActionType.TODAY_SAFE;
        }

        if (reasonCode == PredictionReasonCode.LOW_QUALITY_OPEN) {
            return NextActionType.DO_RECOVERY_ACTION;
        }

        if (reasonCode == PredictionReasonCode.LOW_ACTIVITY_3D) {
            return NextActionType.MINIMUM_CONTACT;
        }

        if (reasonCode == PredictionReasonCode.HABIT_COLLAPSE) {
            return NextActionType.MINIMUM_CONTACT;
        }

        if (reasonCode == PredictionReasonCode.STABLE_PROGRESS) {
            return NextActionType.RECOVERY_CHECK;
        }

        if (reasonCode == PredictionReasonCode.GOOD_PROGRESS) {
            return NextActionType.RECOVERY_CHECK;
        }

        if (reasonCode == PredictionReasonCode.HABIT_STABLE) {
            return NextActionType.RECOVERY_CHECK;
        }

        if (snap != null && snap.state() != null && snap.state().relapseSignalCount() > 0) {
            return NextActionType.PROCESS_RISK_SIGNAL;
        }

        if (snap != null
                && snap.state() != null
                && snap.daysSinceLastEvent() == 0
                && snap.state().recoveryActionCount() < 1) {
            return NextActionType.DO_RECOVERY_ACTION;
        }

        return NextActionType.RECOVERY_CHECK;
    }
}