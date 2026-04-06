package com.goosage.app.predict;

import org.springframework.stereotype.Service;

import com.goosage.api.view.recovery.CoachPredictionReasonCode;
import com.goosage.api.view.recovery.CoachPredictionView;

@Service
public class PredictionCopyService {

    public Copy render(CoachPredictionView p) {
        CoachPredictionReasonCode code = p.reasonCode();

        if (code == CoachPredictionReasonCode.DATA_POOR) {
            return new Copy("상태를 판단할 만큼 데이터가 부족합니다", "초기 상태", "최소 행동 1회");
        }
        if (code == CoachPredictionReasonCode.TODAY_DONE) {
            return new Copy("오늘 회복 흐름이 안정적으로 유지되고 있습니다", "오늘 방어 완료", "유지");
        }
        if (code == CoachPredictionReasonCode.LOW_ACTIVITY_3D) {
            return new Copy("최근 회복 행동이 부족합니다", "비활동 구간", "최소 행동 1회");
        }
        if (code == CoachPredictionReasonCode.HABIT_COLLAPSE) {
            return new Copy("회복 루틴이 무너진 상태입니다", "루틴 붕괴", "즉시 행동 1회");
        }
        if (code == CoachPredictionReasonCode.REVIEW_WRONG_PENDING) {
            return new Copy("위험 신호가 아직 처리되지 않았습니다", "위험 미처리", "위험 처리 1회");
        }
        if (code == CoachPredictionReasonCode.WRONG_HEAVY) {
            return new Copy("위험 신호가 과도하게 누적되고 있습니다", "위험 과다", "즉시 차단 행동");
        }
        if (code == CoachPredictionReasonCode.RECOVERY_PROGRESS) {
            return new Copy("회복 흐름이 다시 형성되고 있습니다", "회복 진행중", "행동 1회 유지");
        }
        if (code == CoachPredictionReasonCode.RECOVERY_SAFE) {
            return new Copy("회복 상태가 안정적으로 유지되고 있습니다", "안정 구간", "유지");
        }
        if (code == CoachPredictionReasonCode.LOW_QUALITY_OPEN) {
            return new Copy("접속은 했지만 행동으로 이어지지 않았습니다", "행동 부족", "행동 1회");
        }
        if (code == CoachPredictionReasonCode.MINIMUM_ACTION) {
            return new Copy("최소 행동으로 흐름을 다시 만들 수 있습니다", "최소 개입", "행동 1회");
        }
        if (code == CoachPredictionReasonCode.HABIT_STABLE) {
            return new Copy("회복 루틴이 안정적으로 유지되고 있습니다", "루틴 안정", "유지");
        }
        if (code == CoachPredictionReasonCode.GOOD_PROGRESS) {
            return new Copy("회복 행동이 잘 유지되고 있습니다", "좋은 흐름", "유지");
        }
        if (code == CoachPredictionReasonCode.STABLE_PROGRESS) {
            return new Copy("안정적인 회복 흐름이 이어지고 있습니다", "안정 진행", "유지");
        }
        if (code == CoachPredictionReasonCode.URGE_HIGH) {
            return new Copy("충동 강도가 높은 상태입니다", "고위험 구간", "즉시 행동");
        }
        if (code == CoachPredictionReasonCode.RELAPSE_RISK) {
            return new Copy("재발 위험 구간입니다", "재발 위험", "즉시 개입");
        }

        return new Copy(
                "현재 상태는 유지 가능하지만 방심하면 위험해질 수 있습니다",
                "중간 구간",
                "최소 행동 1회"
        );
    }

    public record Copy(String expectedOutcome, String reason, String minimalAction) {}
}