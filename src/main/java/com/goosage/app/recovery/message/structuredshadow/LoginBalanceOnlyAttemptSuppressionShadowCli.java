package com.goosage.app.recovery.message.structuredshadow;

import java.util.Arrays;
import java.util.List;

public class LoginBalanceOnlyAttemptSuppressionShadowCli {

    public static void main(String[] args) {

        StructuredEventSequenceShadow sequence =
                new StructuredEventSequenceShadow();

        LoginBalanceOnlyAttemptSuppressionShadow resolver =
                new LoginBalanceOnlyAttemptSuppressionShadow();

        List<StructuredEventShadow> events =
                sequence.resolve(Arrays.asList(args));

        System.out.println(
                "loginBalanceOnlyAttemptSuppression="
                + resolver.resolve(events)
        );
    }
}
