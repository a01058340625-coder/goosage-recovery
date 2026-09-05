package com.goosage.app.recovery.message.subject;

public record SubjectResolutionResult(
        SubjectType subject,
        boolean supported,
        String evidence
) {
}