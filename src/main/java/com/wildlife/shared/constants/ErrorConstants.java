package com.wildlife.shared.constants;

/**
 * Error-related constants used throughout the application.
 * Centralizes error codes, messages, and types for consistent error handling.
 */
public final class ErrorConstants {

    private ErrorConstants() {
        // Private constructor to prevent instantiation
    }

    // Error Codes
    public static final String ERROR_RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND";
    public static final String ERROR_ACCESS_DENIED = "ACCESS_DENIED";
    public static final String ERROR_USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String ERROR_AUTHENTICATION_FAILED = "AUTHENTICATION_FAILED";
    public static final String ERROR_VALIDATION_FAILED = "VALIDATION_FAILED";
    public static final String ERROR_CONSTRAINT_VIOLATION = "CONSTRAINT_VIOLATION";
    public static final String ERROR_MISSING_PARAMETER = "MISSING_PARAMETER";
    public static final String ERROR_TYPE_MISMATCH = "TYPE_MISMATCH";
    public static final String ERROR_MALFORMED_JSON = "MALFORMED_JSON";
    public static final String ERROR_METHOD_NOT_ALLOWED = "METHOD_NOT_ALLOWED";
    public static final String ERROR_ENDPOINT_NOT_FOUND = "ENDPOINT_NOT_FOUND";
    public static final String ERROR_FILE_SIZE_EXCEEDED = "FILE_SIZE_EXCEEDED";
    public static final String ERROR_DATA_INTEGRITY_VIOLATION = "DATA_INTEGRITY_VIOLATION";
    public static final String ERROR_INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";

    // Generic Error Messages
    public static final String MSG_RESOURCE_NOT_FOUND = "Resource not found";
    public static final String MSG_ACCESS_DENIED = "You don't have permission to access this resource";
    public static final String MSG_AUTHENTICATION_FAILED = "Authentication failed. Please check your credentials.";
    public static final String MSG_VALIDATION_FAILED = "Request validation failed";
    public static final String MSG_CONSTRAINT_VIOLATION = "Request constraint validation failed";
    public static final String MSG_MALFORMED_JSON = "Request body is malformed or cannot be parsed";
    public static final String MSG_FILE_SIZE_EXCEEDED = "Uploaded file size exceeds the maximum allowed limit";
    public static final String MSG_DATA_INTEGRITY_VIOLATION = "Data integrity constraint violation occurred";
    public static final String MSG_INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";

    // Parameter Error Messages
    public static final String MSG_MISSING_PARAMETER_TEMPLATE = "Required parameter '%s' is missing";
    public static final String MSG_TYPE_MISMATCH_TEMPLATE = "Parameter '%s' should be of type %s";
    public static final String MSG_METHOD_NOT_ALLOWED_TEMPLATE = "HTTP method '%s' is not supported for this endpoint";
    public static final String MSG_ENDPOINT_NOT_FOUND_TEMPLATE = "No endpoint found for %s %s";

    // Upload Error Messages
    public static final String MSG_NO_FILE_PROVIDED = "No file provided";
    public static final String MSG_NO_IMAGE_FILE_PROVIDED = "No image file provided";
    public static final String MSG_NO_VIDEO_FILE_PROVIDED = "No video file provided";
    public static final String MSG_NO_IMAGE_FILES_PROVIDED = "No image files provided";
    public static final String MSG_IMAGE_SIZE_EXCEEDED = "Image file size exceeds 10MB limit";
    public static final String MSG_VIDEO_SIZE_EXCEEDED = "Video file size exceeds 100MB limit";
    public static final String MSG_TOO_MANY_FILES = "Maximum %d images allowed";
    public static final String MSG_INVALID_IMAGE_TYPE = "Only image files are allowed (JPEG, PNG, WebP, AVIF)";
    public static final String MSG_INVALID_VIDEO_TYPE = "Only video files are allowed (MP4, MOV, AVI, MKV, WebM)";
    public static final String MSG_FILE_NOT_FOUND_OR_DELETED = "File not found or already deleted";

    // Upload Failure Messages  
    public static final String MSG_IMAGE_UPLOAD_FAILED = "Image upload failed: %s";
    public static final String MSG_VIDEO_UPLOAD_FAILED = "Video upload failed: %s";
    public static final String MSG_IMAGES_UPLOAD_FAILED = "Images upload failed: %s";
    public static final String MSG_DELETE_FILE_FAILED = "Failed to delete file: %s";
    public static final String MSG_TRANSFORM_IMAGE_FAILED = "Failed to transform image: %s";

    // Error Type Labels for Logging
    public static final String LOG_RESOURCE_NOT_FOUND = "Resource not found";
    public static final String LOG_ACCESS_DENIED = "Access denied";
    public static final String LOG_SPRING_ACCESS_DENIED = "Spring Security access denied";
    public static final String LOG_USER_ALREADY_EXISTS = "User already exists";
    public static final String LOG_AUTHENTICATION_FAILED = "Authentication failed";
    public static final String LOG_VALIDATION_FAILED = "Validation failed";
    public static final String LOG_CONSTRAINT_VIOLATION = "Constraint violation";
    public static final String LOG_MISSING_PARAMETER = "Missing parameter";
    public static final String LOG_TYPE_MISMATCH = "Type mismatch";
    public static final String LOG_MALFORMED_JSON = "Malformed JSON";
    public static final String LOG_METHOD_NOT_SUPPORTED = "Method not supported";
    public static final String LOG_ENDPOINT_NOT_FOUND = "Endpoint not found";
    public static final String LOG_FILE_SIZE_EXCEEDED = "File size exceeded";
    public static final String LOG_DATA_INTEGRITY_VIOLATION = "Data integrity violation";

    // Special Values
    public static final String CLOUDINARY_SUCCESS_RESULT = "ok";
} 