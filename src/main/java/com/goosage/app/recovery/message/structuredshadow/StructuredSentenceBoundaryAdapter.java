package com.goosage.app.recovery.message.structuredshadow;

import java.util.ArrayList;
import java.util.List;

public class StructuredSentenceBoundaryAdapter {

    public List<String> split(String message) {

        List<String> result =
                new ArrayList<>();

        if (message == null) {
            return result;
        }

        String normalized =
                message.trim()
                        .replaceAll("\\s+", " ");

        if (normalized.isBlank()) {
            return result;
        }

        String[] parts =
                normalized.split(
                        "(?<=[.!?])\\s+"
                );

        for (String part : parts) {

            String value =
                    part.trim();

            if (!value.isBlank()) {
                result.add(value);
            }
        }

        return result;
    }
}
