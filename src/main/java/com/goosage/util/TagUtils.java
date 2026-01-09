package com.goosage.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagUtils {

    // "a,b,c" → List<String>
    public static List<String> parse(String tags) {
        if (tags == null || tags.isBlank()) return List.of();
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // List<String> → "a,b,c"
    public static String join(List<String> tags) {
        if (tags == null || tags.isEmpty()) return "";
        return tags.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }
}
