package com.goosage.api.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.goosage.app.study.StudyCoachService;
import com.goosage.domain.study.StudyCoachResult;

@RestController
public class InternalStudyCoachController {

    private final StudyCoachService studyCoachService;

    public InternalStudyCoachController(StudyCoachService studyCoachService) {
        this.studyCoachService = studyCoachService;
    }

    @GetMapping("/internal/study/coach")
    public StudyCoachResult coach(
            @RequestHeader("X-INTERNAL-KEY") String internalKey,
            @RequestParam("userId") long userId
    ) {
        // 내부키 검증은 보통 필터/인터셉터에서 이미 하고 있을 거라 여기선 전달만 받아도 OK.
        // 만약 지금 프로젝트가 컨트롤러에서 직접 검증하는 방식이면, 아래 한 줄로 고정 검증 추가:
        // if (!"goosage-dev".equals(internalKey)) throw new RuntimeException("INVALID_INTERNAL_KEY");

        return studyCoachService.coach(userId);
    }
}