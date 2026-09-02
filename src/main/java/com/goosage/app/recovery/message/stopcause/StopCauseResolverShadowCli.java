package com.goosage.app.recovery.message.stopcause;

public class StopCauseResolverShadowCli {

    public static void main(String[] args) {
        String message =
                args.length == 0 ? "" : args[0];

        StopCauseResolutionResult result =
                new StopCauseResolverShadow()
                        .resolve(message);

        System.out.println(
                "stopCause=" + result.stopCause()
        );
        System.out.println(
                "confidence=" + result.confidence()
        );
        System.out.println(
                "evidence="
                        + String.join("|", result.evidence())
        );
    }
}
