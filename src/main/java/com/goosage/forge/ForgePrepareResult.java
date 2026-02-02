package com.goosage.forge;

public record ForgePrepareResult(
        ForgeStatus status,
        String message,
        Long templateId
) {
    public static ForgePrepareResult reused(Long templateId) {
        return new ForgePrepareResult(ForgeStatus.REUSE, "reuse", templateId);
    }

    public static ForgePrepareResult created(Long templateId) {
        return new ForgePrepareResult(ForgeStatus.CREATED, "created", templateId);
    }

    public static ForgePrepareResult skipped(String reason) {
        return new ForgePrepareResult(ForgeStatus.SKIPPED, reason, null);
    }

    public static ForgePrepareResult failed(String reason) {
        return new ForgePrepareResult(ForgeStatus.FAILED, reason, null);
    }
}
