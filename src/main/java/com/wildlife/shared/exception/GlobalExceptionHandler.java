package com.wildlife.shared.exception;

import com.wildlife.shared.constants.ErrorConstants;
import com.wildlife.shared.constants.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Global exception handler for all API endpoints.
 * Catches and formats all exceptions into standardized ErrorResponse objects.
 * 
 * This follows enterprise patterns used by tech giants like Google, Amazon, and Netflix:
 * - Consistent error response format
 * - Proper logging with trace IDs
 * - Security-aware error messages
 * - Comprehensive exception coverage
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    /**
     * Handle custom ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ErrorConstants.ERROR_RESOURCE_NOT_FOUND,
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );

        logError(traceId, ErrorConstants.LOG_RESOURCE_NOT_FOUND, ex, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle custom AccessDeniedException
     */
    @ExceptionHandler(com.wildlife.shared.exception.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleCustomAccessDeniedException(
            com.wildlife.shared.exception.AccessDeniedException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            ErrorConstants.ERROR_ACCESS_DENIED,
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );

        logError(traceId, ErrorConstants.LOG_ACCESS_DENIED, ex, request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle Spring Security AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSpringAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            ErrorConstants.ERROR_ACCESS_DENIED,
            ErrorConstants.MSG_ACCESS_DENIED,
            request.getRequestURI(),
            traceId
        );

        logError(traceId, ErrorConstants.LOG_SPRING_ACCESS_DENIED, ex, request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle UserAlreadyExistsException
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            ErrorConstants.ERROR_USER_ALREADY_EXISTS,
            ex.getMessage(),
            request.getRequestURI(),
            traceId
        );

        logError(traceId, ErrorConstants.LOG_USER_ALREADY_EXISTS, ex, request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "AUTHENTICATION_FAILED",
            "Authentication failed. Please check your credentials.",
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "Authentication failed", ex, request);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle validation errors from @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "VALIDATION_FAILED",
            "Request validation failed",
            request.getRequestURI(),
            traceId
        );

        // Extract field errors
        List<ErrorResponse.FieldError> fieldErrors = extractFieldErrors(ex.getBindingResult());
        errorResponse.setFieldErrors(fieldErrors);

        logError(traceId, "Validation failed", ex, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle constraint violations from @Validated
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "CONSTRAINT_VIOLATION",
            "Request constraint validation failed",
            request.getRequestURI(),
            traceId
        );

        // Extract constraint violations
        List<ErrorResponse.FieldError> fieldErrors = ex.getConstraintViolations().stream()
            .map(this::mapConstraintViolation)
            .collect(Collectors.toList());
        errorResponse.setFieldErrors(fieldErrors);

        logError(traceId, "Constraint violation", ex, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "MISSING_PARAMETER",
            String.format("Required parameter '%s' is missing", ex.getParameterName()),
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "Missing parameter", ex, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle type mismatch exceptions
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "TYPE_MISMATCH",
            String.format("Parameter '%s' should be of type %s", 
                ex.getName(), ex.getRequiredType().getSimpleName()),
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "Type mismatch", ex, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle malformed JSON requests
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "MALFORMED_JSON",
            "Request body is malformed or cannot be parsed",
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "Malformed JSON", ex, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle unsupported HTTP methods
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            "METHOD_NOT_ALLOWED",
            String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod()),
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "Method not supported", ex, request);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    /**
     * Handle no handler found (404 errors)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "ENDPOINT_NOT_FOUND",
            String.format("No endpoint found for %s %s", ex.getHttpMethod(), ex.getRequestURL()),
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "Endpoint not found", ex, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle file upload size exceeded
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.PAYLOAD_TOO_LARGE.value(),
            "FILE_SIZE_EXCEEDED",
            "Uploaded file size exceeds the maximum allowed limit",
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "File size exceeded", ex, request);
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(errorResponse);
    }

    /**
     * Handle database constraint violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        
        // In development, expose more details about the constraint violation
        String message = "Data integrity constraint violation occurred";
        String errorCode = "DATA_INTEGRITY_VIOLATION";
        
        boolean isDevelopment = request.getRequestURL().toString().contains("localhost") || 
                               request.getServerName().equals("localhost");
        
        if (isDevelopment) {
            // Extract the root cause message which usually contains the constraint details
            Throwable rootCause = ex.getRootCause();
            String detailMessage = rootCause != null ? rootCause.getMessage() : ex.getMessage();
            message = "DEV CONSTRAINT ERROR: " + detailMessage;
            errorCode = "DEV_DATA_INTEGRITY_VIOLATION";
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            errorCode,
            message,
            request.getRequestURI(),
            traceId
        );

        logError(traceId, "Data integrity violation", ex, request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle all other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        String traceId = generateTraceId();
        
        // In development, expose more details
        String message = "An unexpected error occurred. Please try again later.";
        String errorCode = "INTERNAL_SERVER_ERROR";
        
        // Check if we're in development mode by checking active profiles
        String[] activeProfiles = org.springframework.core.env.Environment.class.isInstance(request) ? 
            new String[0] : new String[]{"dev"}; // This is a simplified check
        
        // For development, expose the actual error
        boolean isDevelopment = request.getRequestURL().toString().contains("localhost") || 
                               request.getServerName().equals("localhost");
        
        if (isDevelopment) {
            message = "DEV ERROR: " + ex.getClass().getSimpleName() + ": " + ex.getMessage();
            errorCode = "DEV_" + ex.getClass().getSimpleName().toUpperCase();
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            errorCode,
            message,
            request.getRequestURI(),
            traceId
        );

        // Log full stack trace for unexpected errors
        logger.error("Unexpected error [TraceId: {}] [Path: {}] [Method: {}]", 
            traceId, request.getRequestURI(), request.getMethod(), ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Extract field errors from BindingResult
     */
    private List<ErrorResponse.FieldError> extractFieldErrors(BindingResult bindingResult) {
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        
        bindingResult.getFieldErrors().forEach(error -> {
            fieldErrors.add(new ErrorResponse.FieldError(
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage()
            ));
        });
        
        bindingResult.getGlobalErrors().forEach(error -> {
            fieldErrors.add(new ErrorResponse.FieldError(
                error.getObjectName(),
                null,
                error.getDefaultMessage()
            ));
        });
        
        return fieldErrors;
    }

    /**
     * Map ConstraintViolation to FieldError
     */
    private ErrorResponse.FieldError mapConstraintViolation(ConstraintViolation<?> violation) {
        String fieldName = violation.getPropertyPath().toString();
        return new ErrorResponse.FieldError(
            fieldName,
            violation.getInvalidValue(),
            violation.getMessage()
        );
    }

    /**
     * Generate unique trace ID for request tracking
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Log error with structured information
     */
    private void logError(String traceId, String errorType, Exception ex, HttpServletRequest request) {
        try {
            MDC.put(SecurityConstants.MDC_TRACE_ID_KEY, traceId);
            logger.error("{} [TraceId: {}] [Path: {}] [Method: {}] [User: {}] - {}", 
                errorType, traceId, request.getRequestURI(), request.getMethod(), 
                getCurrentUser(), ex.getMessage(), ex);
        } finally {
            MDC.clear();
        }
    }

    /**
     * Get current authenticated user for logging (safely)
     */
    private String getCurrentUser() {
        try {
            return org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();
        } catch (Exception e) {
            return SecurityConstants.ANONYMOUS_USER;
        }
    }
} 