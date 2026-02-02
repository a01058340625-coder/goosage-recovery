package com.goosage.service.study.action;

public record NextActionDto(
        NextActionType type,
        String label,
        Long knowledgeId,
        boolean requiresForge,
        String reason
) {
	public static NextActionDto justOpenFallback() {
	    return new NextActionDto(
	            NextActionType.JUST_OPEN,
	            "JUST_OPEN",
	            null,
	            false,
	            "forge failed -> fallback"
	    );
	}

}
