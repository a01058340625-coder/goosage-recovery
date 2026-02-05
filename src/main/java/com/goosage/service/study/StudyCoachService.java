package com.goosage.service.study;

import org.springframework.stereotype.Service;

import com.goosage.dto.study.NextActionView;
import com.goosage.dto.study.PredictionDto;
import com.goosage.dto.study.StudyCoachResponse;
import com.goosage.dto.study.StudyStateView;
import com.goosage.service.study.action.NextActionDto;
import com.goosage.service.study.action.NextActionService;
import com.goosage.service.study.dto.StudyStateDto;
import com.goosage.service.study.interpret.StudyInterpretationService;

@Service
public class StudyCoachService {

    private final StudyInterpretationService studyInterpretationService;
    private final NextActionService nextActionService;

    public StudyCoachService(
            StudyInterpretationService studyInterpretationService,
            NextActionService nextActionService
    ) {
        this.studyInterpretationService = studyInterpretationService;
        this.nextActionService = nextActionService;
    }

    public StudyCoachResponse coach(Long userId) {

        StudyStateDto state = studyInterpretationService.getState(userId);
        NextActionDto nextAction = nextActionService.decide(state);

        StudyCoachResponse response = new StudyCoachResponse();
        response.setState(toView(state));
        response.setNextAction(toView(nextAction));
        response.setPrediction(defaultPrediction());

        return response;
    }

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

    private PredictionDto defaultPrediction() {
        return new PredictionDto("UNCERTAIN", "SHORT", "rule=baseline");
    }
}
