package com.goosage.app.study;

import org.springframework.stereotype.Service;

import com.goosage.domain.study.StudyCoachPort;
import com.goosage.domain.study.StudyCoachResult;

/**
 * Use-case (app)
 * - infra 직접 import 금지
 * - Port만 통해 실행
 */
@Service
public class StudyCoachService {

    private final StudyCoachPort studyCoachPort;

    public StudyCoachService(StudyCoachPort studyCoachPort) {
        this.studyCoachPort = studyCoachPort;
    }

    public StudyCoachResult coach(long userId) {
        return studyCoachPort.execute(userId);
    }
}
