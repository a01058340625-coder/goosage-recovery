package com.goosage.app.recovery.message.event;

public class EventDescriptorResolverShadowCli {

    public static void main(String[] args) {
        String eventText =
                args.length == 0 ? "" : args[0];

        EventDescriptor result =
                new EventDescriptorResolverShadow()
                        .resolve(eventText);

        System.out.println(
                "eventType=" + result.eventType()
        );

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
                "stopCause=" + result.stopCause()
        );
    }
}
