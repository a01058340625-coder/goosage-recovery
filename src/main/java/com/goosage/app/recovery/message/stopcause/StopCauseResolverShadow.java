package com.goosage.app.recovery.message.stopcause;

import java.util.ArrayList;
import java.util.List;

public class StopCauseResolverShadow {

    public StopCauseResolutionResult resolve(
            StopCauseEventInput input
    ) {
        if (input == null) {
            return unknown();
        }

        String combined = String.join(
                " ",
                safe(input.eventText()),
                safe(input.actionText()),
                safe(input.outcomeText())
        );

        return resolve(combined);
    }

    public StopCauseResolutionResult resolve(String message) {
        String normalized = normalize(message);

        if (normalized.isBlank()) {
            return unknown();
        }

        List<String> technical =
                findEvidence(
                        normalized,
                        "\uc624\ub958",
                        "\uc5d0\ub7ec",
                        "\uc2e4\ud328\ud588\uc2b5\ub2c8\ub2e4",
                        "\uc2e4\ud328\ud588",
                        "\ucc98\ub9ac\ub418\uc9c0 \uc54a\uc558",
                        "\uc5f0\uacb0\ub418\uc9c0 \uc54a",
                        "\uc778\uc99d \ub2e8\uacc4\uc5d0\uc11c"
                );

        if (!technical.isEmpty()) {
            return result(
                    StopCauseType.TECHNICAL_FAILURE,
                    0.95,
                    technical
            );
        }

        List<String> externalInterruption =
                findEvidence(
                        normalized,
                        "\uc804\ud654\uac00 \uc654",
                        "\uc5f0\ub77d\uc774 \uc654",
                        "\uac00\uc871\uc774 \ud734\ub300\ud3f0\uc744 \uac00\uc838",
                        "\uc54c\ub9bc\uc774 \uc6b8\ub824",
                        "\ub204\uac00 \ub4e4\uc5b4\uc640",
                        "\ub204\uac00 \ubd88\ub7ec",
                        "\uc5c5\ubb34 \ub54c\ubb38\uc5d0"
                );

        if (!externalInterruption.isEmpty()) {
            return result(
                    StopCauseType.EXTERNAL_INTERRUPTION,
                    0.90,
                    externalInterruption
            );
        }

        List<String> selfStop =
                findEvidence(
                        normalized,
                        "\uadf8\ub0e5 \ub098\uc654",
                        "\uadf8\ub0e5 \ub2eb\uc558",
                        "\ucc3d\uc744 \ub2eb\uc558",
                        "\uc571\uc744 \ub2eb\uc558",
                        "\ucef4\ud4e8\ud130\ub97c \uaed0",
                        "\ud734\ub300\ud3f0\uc744 \ub0b4\ub824\ub1a8",
                        "\ub9c8\uc74c\uc744 \ubc14\uafd4",
                        "\uba48\ucdc4",
                        "\ucde8\uc18c\ud588",
                        "\ub354 \uc9c4\ud589\ud558\uc9c0 \uc54a\uc558"
                );

        if (!selfStop.isEmpty()) {
            return result(
                    StopCauseType.SELF_STOP,
                    0.90,
                    selfStop
            );
        }

        List<String> externalDistraction =
                findEvidence(
                        normalized,
                        "\ub2e4\ub978 \uc77c\uc744 \ud588",
                        "\uc53b\uace0 \uc794",
                        "\ucd9c\uadfc \uc900\ube44",
                        "\ud1b5\ud654\ud558\uba74\uc11c \ub2e4\ub978 \uc598\uae30",
                        "\uc5c5\ubb34\ub97c \ub9c8\ubb34\ub9ac"
                );

        if (!externalDistraction.isEmpty()) {
            return result(
                    StopCauseType.EXTERNAL_DISTRACTION,
                    0.75,
                    externalDistraction
            );
        }

        List<String> natural =
                findEvidence(
                        normalized,
                        "\uadf8 \ub4a4\ub85c\ub294 \ub2e4\uc2dc",
                        "\uc774\ud6c4\uc5d0\ub294 \ub2e4\uc2dc",
                        "\ubcc4\ub3c4\ub85c \ub2e4\uc2dc \uc811\uc18d\ud558\uc9c0 \uc54a\uc558",
                        "\uadf8\ub0a0\uc740 \uadf8\ub300\ub85c",
                        "\ub2e4\uc74c \ub0a0\uc5d0\ub294 \ubcc4\uc0dd\uac01"
                );

        if (!natural.isEmpty()) {
            return result(
                    StopCauseType.NATURAL_NO_FURTHER_ACTION,
                    0.70,
                    natural
            );
        }

        List<String> completion =
                findEvidence(
                        normalized,
                        "\uc644\ub8cc\ub410",
                        "\uc131\ub9bd\ub410",
                        "\uc785\uae08\ub410",
                        "\ub85c\uadf8\uc778\uc5d0 \uc131\uacf5",
                        "\uc8fc\ubb38\ud588\uace0"
                );

        if (!completion.isEmpty()) {
            return result(
                    StopCauseType.COMPLETION,
                    0.80,
                    completion
            );
        }

        return unknown();
    }

    private StopCauseResolutionResult result(
            StopCauseType type,
            double confidence,
            List<String> evidence
    ) {
        return new StopCauseResolutionResult(
                type,
                confidence,
                List.copyOf(evidence)
        );
    }

    private StopCauseResolutionResult unknown() {
        return new StopCauseResolutionResult(
                StopCauseType.UNKNOWN,
                0.20,
                List.of()
        );
    }

    private List<String> findEvidence(
            String text,
            String... candidates
    ) {
        List<String> result = new ArrayList<>();

        for (String candidate : candidates) {
            if (text.contains(candidate)) {
                result.add(candidate);
            }
        }

        return result;
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    private String normalize(String text) {
        if (text == null) {
            return "";
        }

        return text.trim().replaceAll("\\s+", " ");
    }
}
