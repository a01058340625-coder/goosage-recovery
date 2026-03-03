package com.goosage.infra.service.study.predict.copy;

import org.springframework.stereotype.Service;

import com.goosage.infra.service.study.predict.model.InfraPredictionView;
import com.goosage.infra.service.study.predict.model.PredictionEvidence;
import com.goosage.infra.service.study.predict.model.InfraPredictionReasonCodeView;

@Service
public class PredictionCopyService {

    public Copy render(InfraPredictionView p) {
        InfraPredictionReasonCodeView code = p.reasonCode();
        PredictionEvidence ev = p.evidence(); // (지금은 미사용. v1.4에 근거 표시에 쓸 수 있음)

        return switch (code) {
            case DATA_POOR ->
                new Copy("학습 패턴이 충분하지 않습니다", "최근 데이터 부족", "이벤트 1회");

            case TODAY_DONE ->
                new Copy("현재 학습 흐름을 유지하고 있습니다", "오늘 학습 완료", "유지");

            case GAP_3DAYS ->
                new Copy("학습 흐름이 약해지고 있습니다", "3일 연속 공백", "퀴즈 1문제");

            case GAP_4DAYS ->
                new Copy("학습 흐름이 무너질 가능성이 큽니다", "4일 연속 공백", "퀴즈 1문제");

            case STABLE ->
                new Copy("학습 흐름이 안정적입니다", "안정 상태", "유지");

            default ->
                new Copy(
                    "현재 상태는 유지 가능하지만, 공백이 길어지면 리스크가 커집니다",
                    "명시적 위험 규칙에 해당하지 않음",
                    "이벤트 1회"
                );
        };
    }

    public record Copy(String expectedOutcome, String reason, String minimalAction) {}
}
