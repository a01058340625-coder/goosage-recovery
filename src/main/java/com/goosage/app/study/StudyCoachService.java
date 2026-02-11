package com.goosage.app.study;

import org.springframework.stereotype.Service;

import com.goosage.api.view.study.StudyCoachResponse;
import com.goosage.dto.study.NextActionView;
import com.goosage.dto.study.StudyStateView;
import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.interpret.StudyInterpretationService;
import com.goosage.service.study.predict.PredictionService;
import com.goosage.service.study.predict.mapper.PredictionViewMapper;
import com.goosage.service.study.predict.model.Prediction;
import com.goosage.service.study.predict.model.PredictionInput;
import com.goosage.dao.study.StudyReadDao;


@Service
public class StudyCoachService {

    private final StudyInterpretationService studyInterpretationService;
    private final NextActionService nextActionService;

    // 9단계: 예측 레이어 (결정 엔진 보호)
    private final PredictionService predictionService;
    private final PredictionViewMapper predictionViewMapper; 
    private final StudyReadDao studyReadDao;


    public StudyCoachService(
    	    StudyInterpretationService studyInterpretationService,
    	    NextActionService nextActionService,
    	    PredictionService predictionService,
    	    PredictionViewMapper predictionViewMapper,
    	    StudyReadDao studyReadDao
    	) {
    	    this.studyInterpretationService = studyInterpretationService;
    	    this.nextActionService = nextActionService;
    	    this.predictionService = predictionService;
    	    this.predictionViewMapper = predictionViewMapper;
    	    this.studyReadDao = studyReadDao;
    	}

    public StudyCoachResponse coach(Long userId) {

        // 1) 상태 해석 (단일 진실)
        StudyStateDto state = studyInterpretationService.getState(userId);

        // 2) 다음 행동 결정 (보호: Prediction이 절대 영향 주지 않음)
        NextActionDto nextAction = nextActionService.decide(state);

        // 3) 예측 입력 구성 (v1.4 최소 입력)
        int daysSinceLastEvent = studyReadDao.daysSinceLastEvent(userId);
        int recentEventCount3d = studyReadDao.countEventsRecent3d(userId);

        PredictionInput input = new PredictionInput(
            state.streakDays(),
            daysSinceLastEvent,
            recentEventCount3d
        );



        Prediction prediction = predictionService.predict(input);

        // 4) 응답 조립 (view record)
        return new StudyCoachResponse(
                toView(state),
                toView(nextAction),
                predictionViewMapper.toDto(prediction)
        );
    }

    // ------------------------
    // View Mappers
    // ------------------------

    private StudyStateView toView(StudyStateDto s) {
        return new StudyStateView(
                s.ymd(),
                s.studiedToday(),
                s.streakDays(),
                s.eventsCount(),
                s.quizSubmits(),
                s.wrongReviews(),
                s.lastEventAt(),
                s.recentKnowledgeId()
        );
    }

    private NextActionView toView(NextActionDto a) {
        return new NextActionView(
                a.type(),
                a.label(),
                a.knowledgeId(),
                a.requiresForge(),
                a.reason()
        );
    }

    // ------------------------
    // v1.4 안전판 (정밀 계산은 다음 단계)
    // ------------------------

    private int calcDaysSinceLastEvent(StudyStateDto state) {
        if (state.lastEventAt() == null) {
            return 999; // 사실상 "오래 안 함"
        }
        return state.studiedToday() ? 0 : 1;
    }
}
