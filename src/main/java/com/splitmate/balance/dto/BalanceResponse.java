package com.splitmate.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Net position of one member. Positive means the group owes them; negative means they owe the group.")
public record BalanceResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID memberId,
        @Schema(example = "Ana")
        String memberName,
        @Schema(description = "Positive when this member is owed money, negative when they owe money", example = "60.00")
        BigDecimal balance
) {
}
