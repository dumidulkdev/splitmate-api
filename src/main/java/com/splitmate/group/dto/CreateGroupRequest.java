package com.splitmate.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Creates an empty group. Add members before recording expenses.")
public record CreateGroupRequest(
        @NotBlank(message = "is required")
        @Size(max = 120, message = "must be at most 120 characters")
        @Schema(description = "Display name of the group", example = "Weekend in Porto")
        String name
) {
}
