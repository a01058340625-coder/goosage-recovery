package com.goosage.app.recovery.message.protectiveshadow;

public class ProtectiveOutcomeShadowCli {

    public static void main(String[] args) {
        String text = String.join(" ", args);

        boolean existingSelfStop =
                text.contains("SELF_STOP=true");

        String cleanText =
                text.replace("SELF_STOP=true", "").trim();

        boolean result =
                new ProtectiveOutcomeShadow()
                        .resolve(cleanText, existingSelfStop);

        System.out.println(
                "protectiveOutcome=" + result
        );
    }
}