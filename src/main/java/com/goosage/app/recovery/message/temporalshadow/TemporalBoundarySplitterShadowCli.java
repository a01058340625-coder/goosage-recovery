package com.goosage.app.recovery.message.temporalshadow;

public class TemporalBoundarySplitterShadowCli {

    public static void main(String[] args) {
        String text = String.join(" ", args);

        var parts =
                new TemporalBoundarySplitterShadow().split(text);

        for (int i = 0; i < parts.size(); i++) {
            System.out.println(
                    "part" + (i + 1) + "=" + parts.get(i)
            );
        }
    }
}