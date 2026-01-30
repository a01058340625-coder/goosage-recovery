package com.goosage.service.study.dto;

import java.util.List;
import com.goosage.service.study.action.NextActionDto;

public record StudyCoachResponse(
        StudyStateDto state,
        String interpretation,
        NextActionDto nextAction,

        // ✅ v1.2: forge 결과
        String forgeMode,   // "CREATED" | "REUSE" | "FAILED" | null
        String forgeError,  // 실패 메시지(선택)

        String suggestion,
        List<String> reason
) {
    public StudyCoachResponse {
        if (interpretation == null || interpretation.isBlank()) {
            interpretation = "다음 행동을 실행해.";
        }

        // ✅ forgeMode 기본값은 null 유지(= forge 안 돌았음)
        if (forgeMode != null && forgeMode.isBlank()) {
            forgeMode = null;
        }
        if (forgeError != null && forgeError.isBlank()) {
            forgeError = null;
        }

        if (reason == null || reason.size() != 3) {
            reason = List.of(
                    "상태: (알 수 없음)",
                    "왜: (알 수 없음)",
                    "효과: (알 수 없음)"
            );
        }
    }
}
