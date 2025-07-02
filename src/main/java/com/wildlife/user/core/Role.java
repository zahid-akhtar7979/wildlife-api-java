package com.wildlife.user.core;

/**
 * User roles in the wildlife conservation platform.
 * Defines the access levels and permissions for different user types.
 */
public enum Role {
    /**
     * Administrator with full system access
     */
    ADMIN("Administrator"),
    
    /**
     * Content contributor with limited access
     */
    CONTRIBUTOR("Contributor");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this role has administrative privileges
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Check if this role can create content
     */
    public boolean canCreateContent() {
        return this == ADMIN || this == CONTRIBUTOR;
    }

    /**
     * Get role from string value (case-insensitive)
     */
    public static Role fromString(String role) {
        if (role == null) {
            return CONTRIBUTOR; // Default role
        }
        
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CONTRIBUTOR; // Default to contributor if invalid role
        }
    }
} 