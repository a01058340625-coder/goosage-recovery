package com.goosage.app.recovery.message.subject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SubjectResolverShadowCli {

    public static void main(String[] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "base64 message required"
            );
        }

        String message =
                new String(
                        Base64.getDecoder().decode(
                                args[0]
                        ),
                        StandardCharsets.UTF_8
                );

        SubjectResolutionResult result =
                new SubjectResolverShadow()
                        .resolve(message);

        System.out.println(
                "subject="
                + result.subject()
        );

        System.out.println(
                "supported="
                + result.supported()
        );

        System.out.println(
                "evidence="
                + result.evidence()
        );
    }
}