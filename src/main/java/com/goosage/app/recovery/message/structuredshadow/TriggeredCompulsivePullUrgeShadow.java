package com.goosage.app.recovery.message.structuredshadow;

import java.util.List;

public class TriggeredCompulsivePullUrgeShadow {

    public boolean resolve(
            List<StructuredEventShadow> events
    ) {

        StringBuilder allText = new StringBuilder();

        for (StructuredEventShadow structured : events) {
            allText.append(structured.text()).append(" ");
        }

        String text = allText.toString();


        // ----------------------------------------------------
        // A. Compulsive monitoring / withdrawal-like pull
        // ID125
        // ----------------------------------------------------

        boolean compulsivePull =
                containsAny(
                        text,
                        "안 하면 더 불안",
                        "하루 종일 결과 확인"
                )
                && containsAny(
                        text,
                        "몰래 보고",
                        "사이트를 켜게"
                );

        if (compulsivePull) {
            return true;
        }


        // ----------------------------------------------------
        // B. Stress-linked gambling coping reactivation
        // ID162
        // ----------------------------------------------------

        boolean stressLinkedReactivation =
                containsAny(
                        text,
                        "베팅을 하면서 스트레스를 풀었",
                        "이런 날이면 베팅"
                )
                && containsAny(
                        text,
                        "며칠 전에도 사이트를 검색",
                        "입금 화면까지"
                );

        if (stressLinkedReactivation) {
            return true;
        }


        // ----------------------------------------------------
        // C. Relapse concern -> gambling app discovery
        // ID177
        // ----------------------------------------------------

        boolean relapseConcernAppDiscovery =
                containsAny(
                        text,
                        "예전 습관으로 돌아갈까 봐",
                        "다시 예전 습관으로"
                )
                && containsAny(
                        text,
                        "관련 앱을 찾아서",
                        "앱을 찾아서"
                );

        if (relapseConcernAppDiscovery) {
            return true;
        }


        // ----------------------------------------------------
        // D. Trigger recall -> existing gambling app check
        // ID186
        // ----------------------------------------------------

        boolean triggeredAppCheck =
                containsAny(
                        text,
                        "예전 일이 잠깐 생각났",
                        "이름을 듣는 순간 예전 일"
                )
                && containsAny(
                        text,
                        "관련 앱이 아직 남아 있는지도 찾아봤",
                        "관련 앱이 아직 남아"
                );

        if (triggeredAppCheck) {
            return true;
        }


        // ----------------------------------------------------
        // E. Gambling media trigger -> app discovery
        // ID201
        // ----------------------------------------------------

        boolean mediaTriggerAppDiscovery =
                containsAny(
                        text,
                        "슬롯 하는 영상",
                        "카지노 영상",
                        "베팅하는 영상"
                )
                && containsAny(
                        text,
                        "앱이 있는지 찾아봤",
                        "앱을 찾아봤"
                );

        if (mediaTriggerAppDiscovery) {
            return true;
        }

        return false;
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
