package com.splitmate.expense.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "One participant in a split. value is an amount for EXACT, a percentage for PERCENTAGE, and ignored for EQUAL.")
public record ShareRequest(
        @NotNull(message = "is required")
        @Schema(description = "Member taking this share", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID memberId,
        @DecimalMin(value = "0.00", message = "cannot be negative")
        @Digits(integer = 12, fraction = 2, message = "must have at most 2 decimal places")
        @Schema(description = "Amount for EXACT, percentage for PERCENTAGE. Omit for EQUAL.", example = "30.00")
        BigDecimal value
) {
}
