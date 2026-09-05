package com.goosage.app.recovery.message.eligibility;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class StructuredEligibilityGateShadowCli {

    public static void main(String[] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "base64 message required"
            );
        }

        String message =
                new String(
                        Base64.getDecoder().decode(args[0]),
                        StandardCharsets.UTF_8
                );

        StructuredEligibilityResult result =
                new StructuredEligibilityGateShadow()
                        .resolve(message);

        System.out.println(
                "decision=" + result.decision()
        );

        System.out.println(
                "domain=" + result.domain()
        );

        System.out.println(
                "subject=" + result.subject()
        );

        System.out.println(
                "reason=" + result.reason()
        );
    }
}