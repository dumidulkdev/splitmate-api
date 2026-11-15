package com.splitmate.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Schema(description = "A group and the people in it")
public record GroupResponse(
        @Schema(example = "2d9b0c1a-8e7f-4c6d-9b5a-1f0e2d3c4b5a")
        UUID id,
        @Schema(example = "Weekend in Porto")
        String name,
        @Schema(example = "2026-06-12T18:40:00Z")
        Instant createdAt,
        List<MemberResponse> members
) {
}
