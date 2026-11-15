package com.splitmate.expense.dto;

import com.splitmate.expense.SplitType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "A recorded expense and the share each participant owes")
public record ExpenseResponse(
        @Schema(example = "8c1d2e3f-4a5b-6c7d-8e9f-0a1b2c3d4e5f")
        UUID id,
        @Schema(example = "Dinner at Cervejaria")
        String description,
        @Schema(example = "90.00")
        BigDecimal amount,
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID paidBy,
        @Schema(example = "Ana")
        String paidByName,
        SplitType splitType,
        List<ShareResponse> shares,
        @Schema(example = "2026-06-12T19:05:00Z")
        Instant createdAt
) {
}
