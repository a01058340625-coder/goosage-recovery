package com.goosage.app.recovery.message.signalshadow;

public record ShadowSignalVector(
        int urge,
        int attempt,
        int blocked,
        int recovery,
        int relapse
) {
}
