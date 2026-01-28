package com.goosage.ai;

import org.springframework.stereotype.Component;

@Component
public class AiClient {

    public String summarize(String content) {
        // ⚠️ 지금은 실제 AI 호출 안 함
        // 나중에 OpenAI API 붙일 자리

        return """
        [AI 요약 - 더미]
        - 핵심 개념 요약 1
        - 핵심 개념 요약 2
        - 핵심 개념 요약 3
        """;
    }
}
