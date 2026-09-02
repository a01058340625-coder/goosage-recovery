package com.goosage.app.recovery.message.domain;

public class DomainResolverShadowCli {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println(
                    "Usage: DomainResolverShadowCli <message>"
            );
            System.exit(2);
        }

        DomainResolverShadow resolver =
                new DomainResolverShadow();

        DomainResolutionResult result =
                resolver.resolve(args[0]);

        System.out.println(
                "domain=" + result.domain()
        );
        System.out.println(
                "supported=" + result.supported()
        );
        System.out.println(
                "confidence=" + result.confidence()
        );
        System.out.println(
                "reason=" + result.reason()
        );
        System.out.println(
                "evidenceSource=" + result.evidenceSource()
        );
        System.out.println(
                "lateConfirmation="
                        + result.lateConfirmation()
        );
        System.out.println(
                "evidence="
                        + String.join(
                                "|",
                                result.evidence()
                        )
        );
    }
}