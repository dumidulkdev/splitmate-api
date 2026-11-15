package com.splitmate.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Adds a person to a group")
public record AddMemberRequest(
        @NotBlank(message = "is required")
        @Size(max = 120, message = "must be at most 120 characters")
        @Schema(description = "Name unique within the group, compared ignoring case", example = "Ana")
        String name
) {
}
