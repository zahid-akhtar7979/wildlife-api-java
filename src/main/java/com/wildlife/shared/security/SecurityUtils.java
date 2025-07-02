package com.wildlife.shared.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;

/**
 * Utility class for security-related operations.
 * Provides helper methods for accessing current user information and roles.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    /**
     * Get the current authentication
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Get the current user's UserPrincipal
     */
    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        return getCurrentAuthentication()
                .filter(auth -> auth.getPrincipal() instanceof UserPrincipal)
                .map(auth -> (UserPrincipal) auth.getPrincipal());
    }

    /**
     * Get the current user's ID
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getId);
    }

    /**
     * Get the current user's email
     */
    public static Optional<String> getCurrentUserEmail() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getEmail);
    }

    /**
     * Get the current user's username (email)
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getUsername);
    }

    /**
     * Get the current user's full name
     */
    public static Optional<String> getCurrentUserFullName() {
        return getCurrentUserPrincipal()
                .map(UserPrincipal::getName);
    }

    /**
     * Get current user's authorities
     */
    public static Collection<? extends GrantedAuthority> getCurrentUserAuthorities() {
        return getCurrentAuthentication()
                .map(Authentication::getAuthorities)
                .orElse(null);
    }

    /**
     * Check if current user is authenticated
     */
    public static boolean isAuthenticated() {
        return getCurrentAuthentication()
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(String role) {
        String roleWithPrefix = role.startsWith("ROLE_") ? role : "ROLE_" + role.toUpperCase();
        
        return getCurrentAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(authority -> authority.getAuthority().equals(roleWithPrefix)))
                .orElse(false);
    }

    /**
     * Check if current user has any of the specified roles
     */
    public static boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current user is admin
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user is contributor
     */
    public static boolean isContributor() {
        return hasRole("CONTRIBUTOR");
    }

    /**
     * Check if current user can create content
     */
    public static boolean canCreateContent() {
        return hasAnyRole("ADMIN", "CONTRIBUTOR");
    }
} 