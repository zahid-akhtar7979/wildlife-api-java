package com.wildlife.shared.constants;

/**
 * Security-related constants used throughout the application.
 */
public final class SecurityConstants {

    private SecurityConstants() {
        // Private constructor to prevent instantiation
    }

    // Security Requirements
    public static final String BEARER_AUTH = "bearerAuth";

    // Role Names
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CONTRIBUTOR = "CONTRIBUTOR";

    // Role Expressions for @PreAuthorize
    public static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    public static final String HAS_ROLE_CONTRIBUTOR = "hasRole('CONTRIBUTOR')";
    public static final String HAS_ROLE_ADMIN_OR_CONTRIBUTOR = "hasRole('ADMIN') or hasRole('CONTRIBUTOR')";
    public static final String HAS_ANY_ROLE_ADMIN_CONTRIBUTOR = "hasAnyRole('ADMIN', 'CONTRIBUTOR')";

    // JWT Claims
    public static final String RESOURCE_ACCESS_CLAIM = "resource_access";
    public static final String ROLES_CLAIM = "roles";
    public static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
    public static final String SUB_CLAIM = "sub";
    public static final String EMAIL_CLAIM = "email";

    // Default values
    public static final String ANONYMOUS_USER = "anonymous";
    public static final String UNKNOWN_USER = "Unknown User";

    // HTTP Headers
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    // MDC Keys
    public static final String MDC_TRACE_ID_KEY = "traceId";
} 