package com.splitmate.common;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "400", description = "The request is invalid",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
@ApiResponse(responseCode = "404", description = "The group or member was not found",
        content = @Content(schema = @Schema(implementation = ApiError.class)))
public @interface StandardErrors {
}
