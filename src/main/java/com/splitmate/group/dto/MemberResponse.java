package com.splitmate.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "A person who can pay, share or settle expenses in a group")
public record MemberResponse(
        @Schema(example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(example = "Ana")
        String name
) {
}
