package com.goosage.app.recovery.message.segmentation;

import java.util.ArrayList;
import java.util.List;

public class EventSegmenterShadow {

    public List<EventSegment> segment(String message) {
        if (message == null || message.isBlank()) {
            return List.of();
        }

        String normalized =
                message.trim().replaceAll("\\s+", " ");

        normalized = normalized
                .replace(" ??? ", ". ??? ")
                .replace(" ? ? ", ". ? ? ")
                .replace(" ? ??? ", ". ? ??? ")
                .replace(" ?? ? ", ". ?? ? ")
                .replace(" ??? ", ". ??? ")
                .replace(" ?? ", ". ?? ")
                .replace(" ?? ", ". ?? ");

        String[] parts =
                normalized.split("(?<=[.!?])\\s+");

        List<EventSegment> result =
                new ArrayList<>();

        int index = 0;

        for (String part : parts) {
            String text = part.trim();

            if (text.isBlank()) {
                continue;
            }

            result.add(
                    new EventSegment(
                            index++,
                            text
                    )
            );
        }

        return List.copyOf(result);
    }
}
