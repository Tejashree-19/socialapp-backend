package com.socialapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response object")
public class ErrorResponse {

    @Schema(example = "Too many requests")
    private String message;

    public ErrorResponse() {}

    public ErrorResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}