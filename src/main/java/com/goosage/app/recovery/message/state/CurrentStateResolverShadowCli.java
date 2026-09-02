package com.goosage.app.recovery.message.state;

public class CurrentStateResolverShadowCli {

    public static void main(String[] args) {
        String message =
                args.length == 0 ? "" : args[0];

        CurrentStateShadow result =
                new CurrentStateResolverShadow()
                        .resolve(message);

        System.out.println("domain=" + result.domain());
        System.out.println("actionType=" + result.actionType());
        System.out.println("actionStage=" + result.actionStage());
        System.out.println("completed=" + result.completed());
        System.out.println("stopCause=" + result.stopCause());

        System.out.println(
                "signal="
                        + result.signalVector().urge()
                        + ","
                        + result.signalVector().attempt()
                        + ","
                        + result.signalVector().blocked()
                        + ","
                        + result.signalVector().recovery()
                        + ","
                        + result.signalVector().relapse()
        );
    }
}
