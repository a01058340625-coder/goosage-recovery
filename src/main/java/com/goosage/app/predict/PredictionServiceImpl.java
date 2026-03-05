package com.goosage.app.predict;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.StudySnapshot;
import com.goosage.domain.study.StudyState;
import com.goosage.infra.service.study.predict.PredictionMapper;

@Service
public class PredictionServiceImpl implements PredictionService {

    private final PredictionEngine engine;

    public PredictionServiceImpl(PredictionEngine engine) {
        this.engine = engine;
    }

    // ✅ 기존 계약 유지(임시): PredictionInput -> StudySnapshot 변환이 아직 없으니 "금지" 수준으로 둔다
    //    => 지금은 컴파일 목적 + 호출처 정리 전까지의 안전장치
    @Override
    public com.goosage.api.view.study.InfraPredictionView predict(
            com.goosage.api.view.study.PredictionInput input
    ) {
    	
    	System.out.println("HIT: predict(PredictionInput) called");
        // ⚠️ 여기서 도메인 룰을 돌리면 안 됨 (PredictionInput 기반 룰이 사라졌기 때문)
        // 임시로 DEFAULT_FALLBACK 반환 (호출처를 snapshot 기반으로 전환하면 이 메서드는 삭제 대상)
        return com.goosage.api.view.study.InfraPredictionView.of(
                com.goosage.api.view.study.InfraPredictionLevelView.SAFE,
                com.goosage.api.view.study.InfraPredictionReasonCodeView.DEFAULT_FALLBACK,
                com.goosage.api.view.study.PredictionEvidence.from(input)
        );
    }

    // ✅ 새 계약(권장): StudySnapshot 기반으로 예측
    public com.goosage.api.view.study.InfraPredictionView predict(StudySnapshot snapshot) {
    	
    	System.out.println("HIT: predict(StudySnapshot) called");
    	
        var domainPred = engine.predict(snapshot);
        return PredictionMapper.toInfra(domainPred);
    }

    // ✅ 기존에 넣어둔 "StudyState 단일 진실" 계약은 아직 정보가 부족하니, Snapshot으로 승격시키는 방향 권장
    //    일단은 compile 유지용으로 기본 fallback
    @Override
    public com.goosage.api.view.study.InfraPredictionView predict(StudyState state) {
        return com.goosage.api.view.study.InfraPredictionView.of(
                com.goosage.api.view.study.InfraPredictionLevelView.SAFE,
                com.goosage.api.view.study.InfraPredictionReasonCodeView.DEFAULT_FALLBACK,
                com.goosage.api.view.study.PredictionEvidence.from(
                        com.goosage.api.view.study.PredictionInput.of(
                                0L, 0, 999, state.eventsCount()
                        )
                )
        );
    }
}