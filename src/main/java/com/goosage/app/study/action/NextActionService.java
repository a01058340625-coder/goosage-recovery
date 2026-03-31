package com.goosage.app.study.action;

import org.springframework.stereotype.Service;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.study.StudySnapshot;

@Service
public class NextActionService {

    public NextActionType decide(StudySnapshot snap, PredictionReasonCode reasonCode) {

        if (reasonCode == null) {
            return NextActionType.READ_SUMMARY;
        }

        if (reasonCode == PredictionReasonCode.TODAY_DONE) {
            return NextActionType.TODAY_DONE;
        }

        if (reasonCode == PredictionReasonCode.DATA_POOR) {
            return NextActionType.JUST_OPEN;
        }

        if (reasonCode == PredictionReasonCode.MINIMUM_ACTION) {
            return NextActionType.JUST_OPEN;
        }

        if (reasonCode == PredictionReasonCode.REVIEW_WRONG_PENDING) {
            return NextActionType.REVIEW_WRONG_ONE;
        }

        if (reasonCode == PredictionReasonCode.RECOVERY_PROGRESS) {
            return NextActionType.REVIEW_WRONG_ONE;
        }

        if (reasonCode == PredictionReasonCode.RECOVERY_SAFE) {
            return NextActionType.READ_SUMMARY;
        }

        if (reasonCode == PredictionReasonCode.WRONG_HEAVY) {
            return NextActionType.REVIEW_WRONG_ONE;
        }

        if (reasonCode == PredictionReasonCode.LOW_QUALITY_OPEN) {
            return NextActionType.RETRY_QUIZ;
        }

        if (reasonCode == PredictionReasonCode.STABLE_PROGRESS) {
            return NextActionType.RETRY_QUIZ;
        }

        if (reasonCode == PredictionReasonCode.GOOD_PROGRESS) {
            return NextActionType.READ_SUMMARY;
        }

        if (reasonCode == PredictionReasonCode.HABIT_STABLE) {
            return NextActionType.READ_SUMMARY;
        }

        if (reasonCode == PredictionReasonCode.LOW_ACTIVITY_3D
                || reasonCode == PredictionReasonCode.HABIT_COLLAPSE) {
            return NextActionType.READ_SUMMARY;
        }

        if (snap != null && snap.state() != null && snap.state().wrongReviews() > 0) {
            return NextActionType.REVIEW_WRONG_ONE;
        }

        if (snap != null
                && snap.daysSinceLastEvent() == 0
                && snap.state() != null
                && snap.state().quizSubmits() < 2) {
            return NextActionType.RETRY_QUIZ;
        }

        return NextActionType.READ_SUMMARY;
    }
}