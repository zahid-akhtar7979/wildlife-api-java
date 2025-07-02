package com.wildlife.shared.constants;

import java.util.Set;

/**
 * Upload-related constants used throughout the application.
 * Centralizes Cloudinary configuration, file type validation, and image dimensions.
 */
public final class UploadConstants {

    private UploadConstants() {
        // Private constructor to prevent instantiation
    }

    // File Size Limits (in bytes)
    public static final long MAX_IMAGE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB
    public static final long MAX_VIDEO_SIZE_BYTES = 100 * 1024 * 1024; // 100MB
    public static final int MAX_MULTIPLE_IMAGES = 10;

    // Cloudinary Configuration Keys
    public static final String CLOUDINARY_CLOUD_NAME = "cloud_name";
    public static final String CLOUDINARY_API_KEY = "api_key";
    public static final String CLOUDINARY_API_SECRET = "api_secret";
    public static final String CLOUDINARY_SECURE = "secure";

    // Cloudinary Upload Parameters
    public static final String CLOUDINARY_FOLDER = "folder";
    public static final String CLOUDINARY_PUBLIC_ID = "public_id";
    public static final String CLOUDINARY_TRANSFORMATION = "transformation";
    public static final String CLOUDINARY_EAGER = "eager";
    public static final String CLOUDINARY_EAGER_ASYNC = "eager_async";
    public static final String CLOUDINARY_RESOURCE_TYPE = "resource_type";

    // Cloudinary Transformation Parameters
    public static final String CLOUDINARY_WIDTH = "width";
    public static final String CLOUDINARY_HEIGHT = "height";
    public static final String CLOUDINARY_CROP = "crop";
    public static final String CLOUDINARY_QUALITY = "quality";
    public static final String CLOUDINARY_FETCH_FORMAT = "fetch_format";
    public static final String CLOUDINARY_VIDEO_CODEC = "video_codec";

    // Cloudinary Transformation Values
    public static final String CROP_LIMIT = "limit";
    public static final String CROP_FILL = "fill";
    public static final String QUALITY_AUTO_GOOD = "auto:good";
    public static final String FORMAT_AUTO = "auto";
    public static final String VIDEO_CODEC_H264 = "h264";
    public static final String IMAGE_FORMAT_JPG = "jpg";

    // Cloudinary Resource Types
    public static final String RESOURCE_TYPE_IMAGE = "image";
    public static final String RESOURCE_TYPE_VIDEO = "video";

    // Cloudinary Folders
    public static final String FOLDER_WILDLIFE_IMAGES = "wildlife-images";
    public static final String FOLDER_WILDLIFE_VIDEOS = "wildlife-videos";

    // Image Dimensions - Standard Sizes
    public static final int THUMBNAIL_WIDTH = 300;
    public static final int THUMBNAIL_HEIGHT = 200;
    public static final int MEDIUM_WIDTH = 800;
    public static final int MEDIUM_HEIGHT = 600;
    public static final int LARGE_WIDTH = 1200;
    public static final int LARGE_HEIGHT = 800;

    // Video Dimensions
    public static final int VIDEO_HD_WIDTH = 1280;
    public static final int VIDEO_HD_HEIGHT = 720;
    public static final int VIDEO_THUMBNAIL_WIDTH = 800;
    public static final int VIDEO_THUMBNAIL_HEIGHT = 450;

    // File Type MIME Types
    public static final String MIME_IMAGE_JPEG = "image/jpeg";
    public static final String MIME_IMAGE_JPG = "image/jpg";
    public static final String MIME_IMAGE_PNG = "image/png";
    public static final String MIME_IMAGE_WEBP = "image/webp";
    public static final String MIME_IMAGE_AVIF = "image/avif";

    public static final String MIME_VIDEO_MP4 = "video/mp4";
    public static final String MIME_VIDEO_MOV = "video/mov";
    public static final String MIME_VIDEO_AVI = "video/avi";
    public static final String MIME_VIDEO_MKV = "video/mkv";
    public static final String MIME_VIDEO_WEBM = "video/webm";

    // Allowed File Types
    public static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
        MIME_IMAGE_JPEG,
        MIME_IMAGE_JPG,
        MIME_IMAGE_PNG,
        MIME_IMAGE_WEBP,
        MIME_IMAGE_AVIF
    );

    public static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
        MIME_VIDEO_MP4,
        MIME_VIDEO_MOV,
        MIME_VIDEO_AVI,
        MIME_VIDEO_MKV,
        MIME_VIDEO_WEBM
    );

    // File Prefixes
    public static final String IMAGE_PREFIX = "wildlife_";
    public static final String VIDEO_PREFIX = "wildlife_video_";

    // Random ID Length
    public static final int RANDOM_ID_LENGTH = 15;

    // Cloudinary Response Keys
    public static final String CLOUDINARY_SECURE_URL = "secure_url";
    public static final String CLOUDINARY_RESULT = "result";
    public static final String CLOUDINARY_DURATION = "duration";
    public static final String CLOUDINARY_FORMAT = "format";

    // Size Names
    public static final String SIZE_THUMBNAIL = "thumbnail";
    public static final String SIZE_MEDIUM = "medium";
    public static final String SIZE_LARGE = "large";
    public static final String SIZE_ORIGINAL = "original";
} 