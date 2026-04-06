package com.goosage.api.view.recovery;

import com.goosage.domain.NextActionType;

public record NextActionDto(
        NextActionType type,
        String label,
        Long knowledgeId,
        boolean requiresForge,
        String reason
) {
	public static NextActionDto justOpenFallback() {
	    return new NextActionDto(
	            NextActionType.MINIMUM_CONTACT,
	            "JUST_OPEN",
	            null,
	            false,
	            "forge failed -> fallback"
	    );
	}

}
