package com.wildlife.shared.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Standardized error response format for all API errors.
 * Used by GlobalExceptionHandler to ensure consistent error responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response format")
public class ErrorResponse {

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "Error code for client-side handling", example = "RESOURCE_NOT_FOUND")
    private String error;

    @Schema(description = "Human-readable error message", example = "Article not found with id: 123")
    private String message;

    @Schema(description = "API path where error occurred", example = "/api/v1/articles/123")
    private String path;

    @Schema(description = "Timestamp when error occurred")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp;

    @Schema(description = "Unique trace ID for debugging")
    private String traceId;

    @Schema(description = "Field-specific validation errors")
    private List<FieldError> fieldErrors;

    @Schema(description = "Additional error details for debugging")
    private Map<String, Object> details;

    // Default constructor
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    // Constructor for basic errors
    public ErrorResponse(int status, String error, String message, String path) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    // Constructor with trace ID
    public ErrorResponse(int status, String error, String message, String path, String traceId) {
        this(status, error, message, path);
        this.traceId = traceId;
    }

    // Getters and Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }

    /**
     * Represents a field-specific validation error
     */
    @Schema(description = "Field-specific validation error")
    public static class FieldError {
        @Schema(description = "Field name", example = "email")
        private String field;

        @Schema(description = "Rejected value", example = "invalid-email")
        private Object rejectedValue;

        @Schema(description = "Error message", example = "Email format is invalid")
        private String message;

        public FieldError() {}

        public FieldError(String field, Object rejectedValue, String message) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.message = message;
        }

        // Getters and Setters
        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public void setRejectedValue(Object rejectedValue) {
            this.rejectedValue = rejectedValue;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
} 