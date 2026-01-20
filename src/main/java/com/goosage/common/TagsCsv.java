package com.goosage.common;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TagsCsv {
    private TagsCsv() {}

    public static String join(List<String> tags) {
        if (tags == null) return null;

        List<String> cleaned = tags.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        return cleaned.isEmpty() ? null : String.join(",", cleaned);
    }

    public static List<String> split(String csv) {
        if (csv == null || csv.isBlank()) return List.of();

        List<String> cleaned = java.util.Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .distinct()
                .toList();

        return cleaned.isEmpty() ? List.of() : Collections.unmodifiableList(cleaned);
    }
}
