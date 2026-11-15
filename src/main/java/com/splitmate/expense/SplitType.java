package com.splitmate.expense;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "How an expense total is divided")
public enum SplitType {
    @Schema(description = "Equal cent-accurate shares. Extra cents go to the lowest member id.")
    EQUAL,
    @Schema(description = "Each participant has an amount, and the amounts must sum to the total.")
    EXACT,
    @Schema(description = "Each participant has a percentage, and the percentages must sum to 100.")
    PERCENTAGE
}
