package com.splitmate.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "A payment from one member to another. It reduces what the payer owes and what the receiver is owed.")
public record RecordSettlementRequest(
        @NotNull(message = "is required")
        @Schema(description = "Member who paid", example = "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c")
        UUID fromMemberId,
        @NotNull(message = "is required")
        @Schema(description = "Member who received the payment", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID toMemberId,
        @NotNull(message = "is required")
        @DecimalMin(value = "0.01", message = "must be at least 0.01")
        @Digits(integer = 12, fraction = 2, message = "must have at most 2 decimal places")
        @Schema(example = "40.00")
        BigDecimal amount,
        @Size(max = 200, message = "must be at most 200 characters")
        @Schema(description = "Optional note", example = "Dinner and tickets")
        String note
) {
}
