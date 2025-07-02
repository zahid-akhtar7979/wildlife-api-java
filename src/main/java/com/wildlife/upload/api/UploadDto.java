package com.wildlife.upload.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * DTOs for upload functionality
 */
public class UploadDto {

    /**
     * Image upload response DTO
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Image upload response")
    public static class ImageResponse {
        @Schema(description = "Upload success status", example = "true")
        private boolean success;

        @Schema(description = "Upload message", example = "Image uploaded successfully")
        private String message;

        @Schema(description = "Uploaded image data")
        private ImageData data;

        public ImageResponse() {}

        public ImageResponse(boolean success, String message, ImageData data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public ImageData getData() { return data; }
        public void setData(ImageData data) { this.data = data; }
    }

    /**
     * Video upload response DTO
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Video upload response")
    public static class VideoResponse {
        @Schema(description = "Upload success status", example = "true")
        private boolean success;

        @Schema(description = "Upload message", example = "Video uploaded successfully")
        private String message;

        @Schema(description = "Uploaded video data")
        private VideoData data;

        public VideoResponse() {}

        public VideoResponse(boolean success, String message, VideoData data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public VideoData getData() { return data; }
        public void setData(VideoData data) { this.data = data; }
    }

    /**
     * Multiple images upload response DTO
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Multiple images upload response")
    public static class MultipleImagesResponse {
        @Schema(description = "Upload success status", example = "true")
        private boolean success;

        @Schema(description = "Upload message", example = "3 images uploaded successfully")
        private String message;

        @Schema(description = "Uploaded images data")
        private MultipleImagesData data;

        public MultipleImagesResponse() {}

        public MultipleImagesResponse(boolean success, String message, MultipleImagesData data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public MultipleImagesData getData() { return data; }
        public void setData(MultipleImagesData data) { this.data = data; }
    }

    /**
     * Delete file response DTO
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Delete file response")
    public static class DeleteResponse {
        @Schema(description = "Delete success status", example = "true")
        private boolean success;

        @Schema(description = "Delete message", example = "File deleted successfully")
        private String message;

        public DeleteResponse() {}

        public DeleteResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    /**
     * Transform image response DTO
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Transform image response")
    public static class TransformResponse {
        @Schema(description = "Transform success status", example = "true")
        private boolean success;

        @Schema(description = "Transformed image data")
        private TransformData data;

        public TransformResponse() {}

        public TransformResponse(boolean success, TransformData data) {
            this.success = success;
            this.data = data;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public TransformData getData() { return data; }
        public void setData(TransformData data) { this.data = data; }
    }

    /**
     * Transform image request DTO
     */
    @Schema(description = "Transform image request")
    public static class TransformRequest {
        @Schema(description = "Image width", example = "800")
        private Integer width = 800;

        @Schema(description = "Image height", example = "600")
        private Integer height = 600;

        @Schema(description = "Crop mode", example = "limit", allowableValues = {"fill", "fit", "limit", "scale", "crop"})
        private String crop = "limit";

        @Schema(description = "Image quality", example = "auto:good")
        private String quality = "auto:good";

        public TransformRequest() {}

        // Getters and Setters
        public Integer getWidth() { return width; }
        public void setWidth(Integer width) { this.width = width; }
        public Integer getHeight() { return height; }
        public void setHeight(Integer height) { this.height = height; }
        public String getCrop() { return crop; }
        public void setCrop(String crop) { this.crop = crop; }
        public String getQuality() { return quality; }
        public void setQuality(String quality) { this.quality = quality; }
    }

    // Data classes
    /**
     * Image data with responsive sizes
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Image data with responsive sizes")
    public static class ImageData {
        @Schema(description = "Image data")
        private ImageDetails image;

        public ImageData() {}

        public ImageData(ImageDetails image) {
            this.image = image;
        }

        public ImageDetails getImage() { return image; }
        public void setImage(ImageDetails image) { this.image = image; }
    }

    /**
     * Video data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Video data")
    public static class VideoData {
        @Schema(description = "Video data")
        private VideoDetails video;

        public VideoData() {}

        public VideoData(VideoDetails video) {
            this.video = video;
        }

        public VideoDetails getVideo() { return video; }
        public void setVideo(VideoDetails video) { this.video = video; }
    }

    /**
     * Multiple images data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Multiple images data")
    public static class MultipleImagesData {
        @Schema(description = "List of uploaded images")
        private List<ImageDetails> images;

        public MultipleImagesData() {}

        public MultipleImagesData(List<ImageDetails> images) {
            this.images = images;
        }

        public List<ImageDetails> getImages() { return images; }
        public void setImages(List<ImageDetails> images) { this.images = images; }
    }

    /**
     * Transform data
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Transform data")
    public static class TransformData {
        @Schema(description = "Transformed image URL")
        private String url;

        @Schema(description = "Transformation parameters used")
        private Map<String, Object> transformation;

        public TransformData() {}

        public TransformData(String url, Map<String, Object> transformation) {
            this.url = url;
            this.transformation = transformation;
        }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Map<String, Object> getTransformation() { return transformation; }
        public void setTransformation(Map<String, Object> transformation) { this.transformation = transformation; }
    }

    /**
     * Image details with responsive sizes
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Image details with responsive sizes")
    public static class ImageDetails {
        @Schema(description = "Cloudinary public ID", example = "wildlife_1234567890_abc123")
        private String id;

        @Schema(description = "Original image URL")
        private String url;

        @Schema(description = "Image caption")
        private String caption;

        @Schema(description = "Image alt text")
        private String alt;

        @Schema(description = "Responsive image sizes")
        private ImageSizes sizes;

        public ImageDetails() {}

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
        public String getAlt() { return alt; }
        public void setAlt(String alt) { this.alt = alt; }
        public ImageSizes getSizes() { return sizes; }
        public void setSizes(ImageSizes sizes) { this.sizes = sizes; }
    }

    /**
     * Video details
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Video details")
    public static class VideoDetails {
        @Schema(description = "Cloudinary public ID", example = "wildlife_video_1234567890_abc123")
        private String id;

        @Schema(description = "Video URL")
        private String url;

        @Schema(description = "Video caption")
        private String caption;

        @Schema(description = "Video thumbnail URL")
        private String thumbnail;

        @Schema(description = "Video duration in seconds")
        private Double duration;

        @Schema(description = "Video format")
        private String format;

        public VideoDetails() {}

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getCaption() { return caption; }
        public void setCaption(String caption) { this.caption = caption; }
        public String getThumbnail() { return thumbnail; }
        public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
        public Double getDuration() { return duration; }
        public void setDuration(Double duration) { this.duration = duration; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
    }

    /**
     * Responsive image sizes
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Responsive image sizes")
    public static class ImageSizes {
        @Schema(description = "Thumbnail size (300x200)")
        private String thumbnail;

        @Schema(description = "Medium size (800x600)")
        private String medium;

        @Schema(description = "Large size (1200x800)")
        private String large;

        @Schema(description = "Original size")
        private String original;

        public ImageSizes() {}

        // Getters and Setters
        public String getThumbnail() { return thumbnail; }
        public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
        public String getMedium() { return medium; }
        public void setMedium(String medium) { this.medium = medium; }
        public String getLarge() { return large; }
        public void setLarge(String large) { this.large = large; }
        public String getOriginal() { return original; }
        public void setOriginal(String original) { this.original = original; }
    }
} 