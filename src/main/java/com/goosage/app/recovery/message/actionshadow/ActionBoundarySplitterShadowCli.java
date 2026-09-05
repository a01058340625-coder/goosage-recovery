package com.goosage.app.recovery.message.actionshadow;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ActionBoundarySplitterShadowCli {

    public static void main(String[] args) {
        String text = String.join(" ", args);

        var parts =
                new ActionBoundarySplitterShadow().split(text);

        for (int i = 0; i < parts.size(); i++) {
            String encoded =
                    Base64.getEncoder().encodeToString(
                            parts.get(i).getBytes(StandardCharsets.UTF_8)
                    );

            System.out.println(
                    "part" + (i + 1) + "|text64=" + encoded
            );
        }
    }
}