package com.goosage.app.recovery.message.brain;

public record BrainRecoveryDryRunResult(
        BrainRecoveryDryRunStatus status,
        BrainRecoveryDryRunResponse response,
        String failureCode
) {

    public static BrainRecoveryDryRunResult notRequested() {
        return new BrainRecoveryDryRunResult(
                BrainRecoveryDryRunStatus.NOT_REQUESTED,
                null,
                null
        );
    }

    public static BrainRecoveryDryRunResult available(
            BrainRecoveryDryRunResponse response
    ) {
        if (response == null) {
            throw new IllegalArgumentException(
                    "response is required"
            );
        }

        return new BrainRecoveryDryRunResult(
                BrainRecoveryDryRunStatus.AVAILABLE,
                response,
                null
        );
    }

    public static BrainRecoveryDryRunResult unavailable(
            String failureCode
    ) {
        return new BrainRecoveryDryRunResult(
                BrainRecoveryDryRunStatus.UNAVAILABLE,
                null,
                failureCode == null || failureCode.isBlank()
                        ? "BRAIN_UNAVAILABLE"
                        : failureCode
        );
    }

    public boolean available() {
        return status == BrainRecoveryDryRunStatus.AVAILABLE
                && response != null;
    }
}