package com.goosage.ai;

import org.springframework.stereotype.Component;

@Component
public class AiPromptFactory {

    public String summaryPrompt(String content) {
        return """
        다음 내용을 5줄 이내로 핵심만 요약해줘:

        """ + content;
    }
}
