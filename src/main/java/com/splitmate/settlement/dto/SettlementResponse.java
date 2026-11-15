package com.splitmate.settlement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "A payment that was recorded between two members")
public record SettlementResponse(
        @Schema(example = "9d8c7b6a-5e4f-3a2b-1c0d-9e8f7a6b5c4d")
        UUID id,
        @Schema(example = "7c2f1b4f-2d3e-5f60-9b0c-1d2e3f4a5b6c")
        UUID fromMemberId,
        @Schema(example = "Carla")
        String fromMemberName,
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID toMemberId,
        @Schema(example = "Ana")
        String toMemberName,
        @Schema(example = "40.00")
        BigDecimal amount,
        @Schema(example = "Dinner and tickets")
        String note,
        @Schema(example = "2026-06-12T20:15:00Z")
        Instant createdAt
) {
}
