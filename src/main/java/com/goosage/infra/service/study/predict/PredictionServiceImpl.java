package com.goosage.infra.service.study.predict;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.PredictionRule;
import com.goosage.domain.study.StudyState;
import com.goosage.infra.service.study.predict.model.Prediction;
import com.goosage.infra.service.study.predict.model.PredictionEvidence;
import com.goosage.infra.service.study.predict.model.PredictionInput;
import com.goosage.infra.service.study.predict.model.PredictionLevel;
import com.goosage.infra.service.study.predict.model.PredictionReasonCode;

@Service
public class PredictionServiceImpl implements PredictionService {

    private final List<PredictionRule> rules;

    public PredictionServiceImpl(List<PredictionRule> rules) {
        this.rules = rules.stream()
                .sorted(Comparator.comparingInt(PredictionRule::priority))
                .toList();
    }

    // ✅ 기존 계약 유지
    @Override
    public Prediction predict(PredictionInput input) {
        return rules.stream()
                .filter(r -> r.matches(input))
                .map(r -> r.predict(input))
                .findFirst()
                .orElseGet(() -> defaultPrediction(input));
    }

    // ✅ 새 계약: StudyState 단일 진실 기반
    @Override
    public Prediction predict(StudyState state) {

        // 지금 단계에서는 evidence 전체를 state에서 못 만드니
        // 최소 브릿지 방식으로 기존 로직 재사용

        PredictionInput input = PredictionInput.of(
                0L,                    // userId (추후 state 확장 시 이동)
                0,                     // streakDays (추후 확장)
                999,                   // daysSinceLast (추후 확장)
                state.eventsCount()    // 현재 단일 진실 필드 사용
        );

        return predict(input);
    }

    private Prediction defaultPrediction(PredictionInput input) {
        return Prediction.of(
                PredictionLevel.SAFE,
                PredictionReasonCode.DEFAULT_FALLBACK,
                PredictionEvidence.from(input)
        );
    }
}
