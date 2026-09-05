package com.goosage.app.recovery.message.protectiveshadow;

public class ProtectiveOutcomeShadow {

    private final ProtectiveIncompletionShadow incompletion =
            new ProtectiveIncompletionShadow();

    private final CompletedProtectiveBlockShadow completedBlock =
            new CompletedProtectiveBlockShadow();

    private final ProtectiveSearchInputReversalShadow searchInputReversal =
            new ProtectiveSearchInputReversalShadow();

    public boolean resolve(
            String text,
            boolean existingSelfStop
    ) {
        return existingSelfStop
                || incompletion.resolve(text)
                || completedBlock.resolve(text)
                || searchInputReversal.resolve(text);
    }
}
