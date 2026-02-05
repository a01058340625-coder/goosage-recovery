package com.goosage.service.study.predict.rule;

import org.springframework.stereotype.Component;

import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionInput;
import com.goosage.service.study.predict.model.PredictionLevel;

@Component
public class DataPoorDefaultRule implements PredictionRule {

    @Override
    public boolean matches(PredictionInput i) {
        // "데이터 부족"의 정의는 v1.4 최소 기준: 최근 3일 이벤트가 0이거나, streak도 0
        return i.recentEventCount3d() == 0 && i.streakDays() == 0;
    }

    @Override
    public Prediction predict(PredictionInput i) {
        return new Prediction(
                PredictionLevel.WARNING,
                "학습 패턴이 충분하지 않습니다",
                "최근 데이터 부족",
                "이벤트 1회"
        );
    }

    @Override
    public int priority() {
        // WARNING지만 "default" 성격이라 SAFE보다도 뒤로 보낼지,
        // 아니면 가장 먼저 잡을지 선택인데,
        // v1.4에서는 "명시적 데이터 부족"을 먼저 알리기 위해 앞쪽에 둔다.
        return 5;
    }
}
