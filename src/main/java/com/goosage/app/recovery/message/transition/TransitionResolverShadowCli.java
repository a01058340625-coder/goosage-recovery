package com.goosage.app.recovery.message.transition;

public class TransitionResolverShadowCli {

    public static void main(String[] args) {
        String beforeText =
                args.length > 0 ? args[0] : "";

        String eventText =
                args.length > 1 ? args[1] : "";

        String afterText =
                args.length > 2 ? args[2] : "";

        TransitionShadow result =
                new TransitionResolverShadow()
                        .resolve(
                                beforeText,
                                eventText,
                                afterText
                        );

        System.out.println(
                "beforeAction="
                        + result.beforeState().actionType()
        );

        System.out.println(
                "eventType="
                        + result.event().eventType()
        );

        System.out.println(
                "afterAction="
                        + result.afterState().actionType()
        );

        System.out.println(
                "afterSignal="
                        + result.afterState().signalVector().urge()
                        + ","
                        + result.afterState().signalVector().attempt()
                        + ","
                        + result.afterState().signalVector().blocked()
                        + ","
                        + result.afterState().signalVector().recovery()
                        + ","
                        + result.afterState().signalVector().relapse()
        );
    }
}
