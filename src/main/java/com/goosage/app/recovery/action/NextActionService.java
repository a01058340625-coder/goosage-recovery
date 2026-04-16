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

            if (snap == null || snap.state() == null) {
                return NextActionType.RECOVERY_CHECK;
            }

            int relapse = snap.state().relapseSignalCount();
            int recovery = snap.state().recoveryActionCount();
            int blocked = snap.state().betBlockedCount();
            int attempts = snap.state().betAttempts();
            int urge = snap.state().urgeLogs();
            int events = snap.state().eventsCount();
            int recent3d = snap.recentEventCount3d();

            if (relapse > 0 || attempts > 0 || urge > 0) {
                return NextActionType.PROCESS_RISK_SIGNAL;
            }

            // hold 성격: 얇은 진행은 체크 우선
            if (events <= 1 && recent3d <= 2) {
                return NextActionType.RECOVERY_CHECK;
            }

            // blocked 중심 방어 케이스는 행동 유지
            if (blocked > 0) {
                return NextActionType.DO_RECOVERY_ACTION;
            }

            // recovery는 있지만 아직 얇으면 체크
            if (recovery <= 1 && events <= 1) {
                return NextActionType.RECOVERY_CHECK;
            }

            if (recovery > 0 || blocked > 0) {
                return NextActionType.DO_RECOVERY_ACTION;
            }

            return NextActionType.RECOVERY_CHECK;
        }

        if (reasonCode == PredictionReasonCode.RECOVERY_SAFE) {
            if (snap == null || snap.state() == null) {
                return NextActionType.TODAY_SAFE;
            }

            int events = snap.state().eventsCount();
            int recovery = snap.state().recoveryActionCount();

            // thin safe는 완료가 아니라 점검
            if (events <= 1 || recovery <= 1) {
                return NextActionType.RECOVERY_CHECK;
            }

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