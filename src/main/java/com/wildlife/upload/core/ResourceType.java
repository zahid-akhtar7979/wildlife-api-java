package com.wildlife.entity.enums;

/**
 * Enumeration for resource types in file upload operations.
 * Used for Cloudinary uploads and resource management.
 */
public enum ResourceType {
    
    IMAGE("image", "Image"),
    VIDEO("video", "Video");

    private final String value;
    private final String displayName;

    ResourceType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get ResourceType from string value (case-insensitive)
     */
    public static ResourceType fromString(String value) {
        if (value == null) {
            return IMAGE; // Default to image
        }
        
        for (ResourceType type : ResourceType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        return IMAGE; // Default fallback
    }

    /**
     * Check if this is an image resource type
     */
    public boolean isImage() {
        return this == IMAGE;
    }

    /**
     * Check if this is a video resource type
     */
    public boolean isVideo() {
        return this == VIDEO;
    }

    @Override
    public String toString() {
        return value;
    }
} 