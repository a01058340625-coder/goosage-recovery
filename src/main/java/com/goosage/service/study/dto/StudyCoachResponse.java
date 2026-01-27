package com.goosage.service.study.dto;

import java.util.List;
import com.goosage.service.study.action.NextActionDto;

public record StudyCoachResponse(
	    StudyStateDto state,
	    String interpretation,
	    NextActionDto nextAction,
	    String suggestion,
	    List<String> reason,
	    String nextActionReason
	) {

	    public StudyCoachResponse {
	        if (interpretation == null || interpretation.isBlank()) {
	            interpretation = "다음 행동을 실행해.";
	        }
	        if (reason == null || reason.size() != 3) {
	            reason = List.of(
	                "상태: (알 수 없음)",
	                "왜: (알 수 없음)",
	                "효과: (알 수 없음)"
	            );
	        }
	        if (nextActionReason == null || nextActionReason.isBlank()) {
	            nextActionReason = "다음 행동 1개가 루프를 연다";
	        }
	    }
	}

