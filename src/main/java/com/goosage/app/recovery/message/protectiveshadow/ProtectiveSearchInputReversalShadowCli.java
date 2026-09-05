package com.goosage.app.recovery.message.protectiveshadow;

public class ProtectiveSearchInputReversalShadowCli {

    public static void main(String[] args) {

        ProtectiveSearchInputReversalShadow resolver =
                new ProtectiveSearchInputReversalShadow();

        String text =
                String.join(" ", args);

        System.out.println(
                "protectiveSearchInputReversal="
                + resolver.resolve(text)
        );
    }
}
