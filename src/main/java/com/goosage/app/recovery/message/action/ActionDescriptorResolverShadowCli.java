package com.goosage.app.recovery.message.action;

public class ActionDescriptorResolverShadowCli {

    public static void main(String[] args) {
        String message =
                args.length == 0 ? "" : args[0];

        ActionDescriptor result =
                new ActionDescriptorResolverShadow()
                        .resolve(message);

        System.out.println(
                "actionType=" + result.actionType()
        );

        System.out.println(
                "actionStage=" + result.actionStage()
        );

        System.out.println(
                "completed=" + result.completed()
        );

        System.out.println(
                "confidence=" + result.confidence()
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
