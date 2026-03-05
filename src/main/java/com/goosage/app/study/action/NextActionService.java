package com.goosage.app.study.action;

import org.springframework.stereotype.Service;

import com.goosage.domain.NextActionType;
import com.goosage.domain.predict.PredictionReasonCode;
import com.goosage.domain.study.StudySnapshot;

@Service
public class NextActionService {

    public NextActionType decide(StudySnapshot snap, PredictionReasonCode reasonCode) {

        // 0) 오늘 끝이면 무조건 종료
        if (reasonCode == PredictionReasonCode.TODAY_DONE) {
            return NextActionType.TODAY_DONE;
        }

        // 1) 데이터 부족이면 "한 번 열기"부터 (증거 생성)
        if (reasonCode == PredictionReasonCode.DATA_POOR) {
            return NextActionType.JUST_OPEN;
        }

        // 2) 최근 활동 저하 → 부담 낮은 요약 읽기
        if (reasonCode == PredictionReasonCode.LOW_ACTIVITY_3D) {
            return NextActionType.READ_SUMMARY;
        }

        // 3) 기본값(안전하게) → 요약 읽기
        return NextActionType.READ_SUMMARY;
    }
}