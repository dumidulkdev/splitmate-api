package com.splitmate.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "One payment that reduces the group's debt. fromMember pays toMember.")
public record SettlementSuggestion(
        @Schema(description = "Member who should pay", example = "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c")
        UUID fromMemberId,
        @Schema(example = "Carla")
        String fromMemberName,
        @Schema(description = "Member who should receive the payment", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID toMemberId,
        @Schema(example = "Ana")
        String toMemberName,
        @Schema(example = "40.00")
        BigDecimal amount
) {
}
