package com.wildlife.shared.constants;

/**
 * API-related constants used throughout the application.
 */
public final class ApiConstants {

    private ApiConstants() {
        // Private constructor to prevent instantiation
    }

    // API Paths
    public static final String API_BASE_PATH = "/api";
    public static final String UPLOAD_BASE_PATH = "/api/upload";
    public static final String ARTICLES_BASE_PATH = "/api/articles";
    public static final String USERS_BASE_PATH = "/api/users";

    // Upload Endpoints
    public static final String UPLOAD_IMAGE_PATH = "/image";
    public static final String UPLOAD_VIDEO_PATH = "/video";
    public static final String UPLOAD_MULTIPLE_IMAGES_PATH = "/multiple-images";
    public static final String DELETE_FILE_PATH = "/delete/{publicId}";
    public static final String TRANSFORM_IMAGE_PATH = "/transform-image/{publicId}";

    // Request Parameters
    public static final String PARAM_IMAGE = "image";
    public static final String PARAM_VIDEO = "video";
    public static final String PARAM_IMAGES = "images";
    public static final String PARAM_CAPTION = "caption";
    public static final String PARAM_ALT = "alt";
    public static final String PARAM_RESOURCE_TYPE = "resourceType";

    // Default Parameter Values
    public static final String DEFAULT_EMPTY_STRING = "";
    public static final String DEFAULT_RESOURCE_TYPE = "image";

    // Content Types
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final String APPLICATION_JSON = "application/json";

    // Swagger/OpenAPI Tags
    public static final String TAG_UPLOAD = "Upload";
    public static final String TAG_ARTICLES = "Articles";
    public static final String TAG_USERS = "Users";

    // API Descriptions
    public static final String DESC_UPLOAD = "File upload management for images and videos";
    public static final String DESC_ARTICLES = "Wildlife conservation article management API";
    public static final String DESC_USERS = "User management and profile operations";

    // CORS Configuration
    public static final String CORS_ORIGINS_ALL = "*";
    public static final long CORS_MAX_AGE = 3600L;

    // Pagination Defaults
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int MAX_PAGE_SIZE = 100;

    // Success Messages
    public static final String MSG_IMAGE_UPLOADED = "Image uploaded successfully";
    public static final String MSG_VIDEO_UPLOADED = "Video uploaded successfully";
    public static final String MSG_FILE_DELETED = "File deleted successfully";
    public static final String MSG_IMAGES_UPLOADED_TEMPLATE = "%d images uploaded successfully";

    // Path Variables
    public static final String PATH_VAR_PUBLIC_ID = "publicId";
    public static final String PATH_VAR_ID = "id";
} 