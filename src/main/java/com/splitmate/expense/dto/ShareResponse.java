package com.splitmate.expense.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "The amount one member owes for an expense, after rounding")
public record ShareResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID memberId,
        @Schema(example = "Ana")
        String memberName,
        @Schema(description = "Share of the total in currency units", example = "30.00")
        BigDecimal amount
) {
}
