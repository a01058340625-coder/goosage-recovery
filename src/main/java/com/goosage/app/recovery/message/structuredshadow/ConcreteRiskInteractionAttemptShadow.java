package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class ConcreteRiskInteractionAttemptShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        for (StructuredEventShadow structured : events) {

            String stage =
                    structured.event().actionStage().name();

            String text =
                    structured.text();

            if (stage.equals("THOUGHT")) {
                continue;
            }

            if (hasConcreteInteraction(text)) {
                return true;
            }
        }

        return false;
    }


    private boolean hasConcreteInteraction(
            String text
    ) {
        return containsAny(
                text,
                "누르고 카지노 화면",
                "눌렀는데 카지노 화면",
                "사이트 화면까지",
                "눌러보긴 했",
                "배당도 같이 확인",
                "배당표를 조금 봤",
                "앱을 열었",
                "금액도 입력",
                "설치까지 했",
                "번호를 넣었",
                "로그인에 실패"
        );
    }


    private boolean containsAny(
            String text,
            String... values
    ) {
        for (String value : values) {
            if (text.contains(value)) {
                return true;
            }
        }

        return false;
    }
}
