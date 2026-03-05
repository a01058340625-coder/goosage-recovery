package com.goosage.app.predict;

import org.springframework.stereotype.Service;

import com.goosage.api.view.study.CoachPredictionReasonCode;
import com.goosage.api.view.study.CoachPredictionView;

@Service
public class PredictionCopyService {

    public Copy render(CoachPredictionView p) {
        CoachPredictionReasonCode code = p.reasonCode();

        return switch (code) {
            case DATA_POOR ->
                    new Copy("학습 패턴이 충분하지 않습니다", "최근 데이터 부족", "이벤트 1회");

            case TODAY_DONE ->
                    new Copy("현재 학습 흐름을 유지하고 있습니다", "오늘 학습 완료", "유지");

            case LOW_ACTIVITY_3D ->
                    new Copy("최근 학습 활동이 낮습니다", "최근 3일 활동 저하", "이벤트 1회");

            case HABIT_COLLAPSE ->
                    new Copy("학습 흐름이 무너질 위험이 있습니다", "학습 공백 증가", "이벤트 1회");

            default ->
                    new Copy(
                            "현재 상태는 유지 가능하지만, 공백이 길어지면 리스크가 커집니다",
                            "명시적 규칙 외 구간",
                            "이벤트 1회"
                    );
        };
    }

    public record Copy(String expectedOutcome, String reason, String minimalAction) {}
}