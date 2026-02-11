package com.goosage.app;

import java.util.List;

import org.springframework.stereotype.Service;

import com.goosage.dto.KnowledgeDto;

@Service
public class KnowledgeTemplateService {

    public String toSummaryV1(KnowledgeDto knowledge) {
        String content = knowledge.getContent();

        if (content == null || content.isBlank()) {
            return "[요약]\n(내용 없음)";
        }

        if (content.length() <= 500) {
            return "[요약]\n" + content;
        }

        return "[요약]\n"
            + content.substring(0, 300)
            + "\n...\n"
            + "핵심 요약 문장 1\n"
            + "핵심 요약 문장 2";
    }

    public List<String> toQuizV1(KnowledgeDto knowledge) {
        return List.of(
            "이 지식의 핵심 개념은 무엇인가?",
            "왜 이 개념이 중요한가?",
            "실제 적용 예시는 무엇인가?"
        );
    }
    public String toSummaryV2(KnowledgeDto knowledge) {
        return "[SUMMARY_V2]\n" + (knowledge.getContent() == null ? "" : knowledge.getContent());
    }

    public String toQuizV2Text(KnowledgeDto knowledge) {
        return "[QUIZ_V2]\n"
            + "1) 핵심 개념은?\n"
            + "2) 왜 중요한가?\n"
            + "3) 예시는?\n";
    }

}
