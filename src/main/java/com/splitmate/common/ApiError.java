package com.splitmate.common;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Body returned for every failed request")
public record ApiError(
        @Schema(example = "2026-06-12T18:41:02.123Z")
        Instant timestamp,
        @Schema(example = "400")
        int status,
        @Schema(example = "Bad Request")
        String error,
        @Schema(example = "Exact amounts sum to 80.00 but the expense is 90.00")
        String message,
        @Schema(example = "/api/groups/3fa85f64-5717-4562-b3fc-2c963f66afa6/expenses")
        String path,
        List<FieldError> fieldErrors
) {

    public ApiError {
        fieldErrors = fieldErrors == null ? List.of() : List.copyOf(fieldErrors);
    }

    public static ApiError of(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        return new ApiError(Instant.now(), status, error, message, path, fieldErrors);
    }

    @Schema(description = "One field that failed validation")
    public record FieldError(
            @Schema(example = "amount")
            String field,
            @Schema(example = "must be at least 0.01")
            String message
    ) {
    }
}
