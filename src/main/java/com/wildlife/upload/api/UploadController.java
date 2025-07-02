package com.wildlife.upload.api;

import com.wildlife.shared.constants.ApiConstants;
import com.wildlife.shared.constants.SecurityConstants;
import com.wildlife.upload.service.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for handling file upload operations.
 * Implements comprehensive upload functionality for images and videos.
 */
@RestController
@RequestMapping(ApiConstants.UPLOAD_BASE_PATH)
@PreAuthorize(SecurityConstants.HAS_ANY_ROLE_ADMIN_CONTRIBUTOR)
@CrossOrigin(origins = ApiConstants.CORS_ORIGINS_ALL, maxAge = ApiConstants.CORS_MAX_AGE)
public class UploadController implements UploadApi {

    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    private final UploadService uploadService;

    @Autowired
    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    /**
     * Upload a single image
     * POST /api/upload/image
     */
    @Override
    @PostMapping(value = ApiConstants.UPLOAD_IMAGE_PATH, consumes = {ApiConstants.MULTIPART_FORM_DATA})
    public ResponseEntity<UploadDto.ImageResponse> uploadImage(
            @RequestParam(ApiConstants.PARAM_IMAGE) MultipartFile image,
            @RequestParam(value = ApiConstants.PARAM_CAPTION, required = false, defaultValue = ApiConstants.DEFAULT_EMPTY_STRING) String caption,
            @RequestParam(value = ApiConstants.PARAM_ALT, required = false, defaultValue = ApiConstants.DEFAULT_EMPTY_STRING) String alt) {
        
        logger.info("🖼️ Image upload request received: {}", image.getOriginalFilename());
        
        try {
            UploadDto.ImageResponse response = uploadService.uploadImage(image, caption, alt);
            logger.info("✅ Image upload completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Image upload validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Image upload failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Upload a single video
     * POST /api/upload/video
     */
    @Override
    @PostMapping(value = ApiConstants.UPLOAD_VIDEO_PATH, consumes = {ApiConstants.MULTIPART_FORM_DATA})
    public ResponseEntity<UploadDto.VideoResponse> uploadVideo(
            @RequestParam(ApiConstants.PARAM_VIDEO) MultipartFile video,
            @RequestParam(value = ApiConstants.PARAM_CAPTION, required = false, defaultValue = ApiConstants.DEFAULT_EMPTY_STRING) String caption) {
        
        logger.info("🎥 Video upload request received: {}", video.getOriginalFilename());
        
        try {
            UploadDto.VideoResponse response = uploadService.uploadVideo(video, caption);
            logger.info("✅ Video upload completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Video upload validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Video upload failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Upload multiple images
     * POST /api/upload/multiple-images
     */
    @Override
    @PostMapping(value = ApiConstants.UPLOAD_MULTIPLE_IMAGES_PATH, consumes = {ApiConstants.MULTIPART_FORM_DATA})
    public ResponseEntity<UploadDto.MultipleImagesResponse> uploadMultipleImages(
            @RequestParam(ApiConstants.PARAM_IMAGES) List<MultipartFile> images) {
        
        logger.info("🖼️📦 Multiple images upload request received: {} files", images.size());
        
        try {
            UploadDto.MultipleImagesResponse response = uploadService.uploadMultipleImages(images);
            logger.info("✅ Multiple images upload completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("⚠️ Multiple images upload validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("❌ Multiple images upload failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete a file from Cloudinary
     * DELETE /api/upload/delete/{publicId}
     */
    @Override
    @DeleteMapping(ApiConstants.DELETE_FILE_PATH)
    public ResponseEntity<UploadDto.DeleteResponse> deleteFile(
            @PathVariable String publicId,
            @RequestParam(value = ApiConstants.PARAM_RESOURCE_TYPE, defaultValue = ApiConstants.DEFAULT_RESOURCE_TYPE) String resourceType) {
        
        logger.info("🗑️ Delete file request received: {} (type: {})", publicId, resourceType);
        
        try {
            UploadDto.DeleteResponse response = uploadService.deleteFile(publicId, resourceType);
            logger.info("✅ File deletion completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ File deletion failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get transformed image URL
     * POST /api/upload/transform-image/{publicId}
     */
    @Override
    @PostMapping(ApiConstants.TRANSFORM_IMAGE_PATH)
    public ResponseEntity<UploadDto.TransformResponse> transformImage(
            @PathVariable String publicId,
            @RequestBody UploadDto.TransformRequest transformRequest) {
        
        logger.info("🔄 Transform image request received: {} with params: {}x{}", 
            publicId, transformRequest.getWidth(), transformRequest.getHeight());
        
        try {
            UploadDto.TransformResponse response = uploadService.transformImage(publicId, transformRequest);
            logger.info("✅ Image transformation completed successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Image transformation failed: {}", e.getMessage(), e);
            throw e;
        }
    }
} 