package com.splitmate.expense.dto;

import com.splitmate.expense.SplitType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Schema(description = "An expense paid by one member and split across participants")
public record CreateExpenseRequest(
        @NotBlank(message = "is required")
        @Size(max = 200, message = "must be at most 200 characters")
        @Schema(example = "Dinner at Cervejaria")
        String description,
        @NotNull(message = "is required")
        @DecimalMin(value = "0.01", message = "must be at least 0.01")
        @Digits(integer = 12, fraction = 2, message = "must have at most 2 decimal places")
        @Schema(description = "Total paid, in the group's currency", example = "90.00")
        BigDecimal amount,
        @NotNull(message = "is required")
        @Schema(description = "Member who paid the total", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID paidBy,
        @NotNull(message = "is required")
        @Schema(example = "EQUAL")
        SplitType splitType,
        @NotEmpty(message = "must include at least one participant")
        List<@Valid ShareRequest> shares
) {
}
