package com.wildlife.upload.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.wildlife.shared.constants.ApiConstants;
import com.wildlife.shared.constants.ErrorConstants;
import com.wildlife.shared.constants.UploadConstants;
import com.wildlife.upload.api.UploadDto;
import com.wildlife.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * Service for handling file uploads to Cloudinary.
 * Provides comprehensive functionality for image and video management.
 */
@Service
public class UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);

    private final Cloudinary cloudinary;

    // File size limits and allowed types are now in UploadConstants

    @Autowired
    public UploadService(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            UploadConstants.CLOUDINARY_CLOUD_NAME, cloudName,
            UploadConstants.CLOUDINARY_API_KEY, apiKey,
            UploadConstants.CLOUDINARY_API_SECRET, apiSecret,
            UploadConstants.CLOUDINARY_SECURE, true
        ));
        
        logger.info("Cloudinary service initialized with cloud: {}", cloudName);
    }

    /**
     * Upload a single image to Cloudinary
     */
    @SuppressWarnings("unchecked")
    public UploadDto.ImageResponse uploadImage(MultipartFile file, String caption, String alt) {
        logger.info("🖼️ Starting image upload: {}", file.getOriginalFilename());
        
        try {
            // Validate image file
            validateImageFile(file);
            
            // Generate unique public ID
            String publicId = generateImagePublicId();
            
            // Upload to Cloudinary with transformations
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                UploadConstants.CLOUDINARY_FOLDER, UploadConstants.FOLDER_WILDLIFE_IMAGES,
                UploadConstants.CLOUDINARY_PUBLIC_ID, publicId,
                UploadConstants.CLOUDINARY_TRANSFORMATION, Arrays.asList(
                    ObjectUtils.asMap(
                        UploadConstants.CLOUDINARY_WIDTH, UploadConstants.LARGE_WIDTH,
                        UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.LARGE_HEIGHT,
                        UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_LIMIT,
                        UploadConstants.CLOUDINARY_QUALITY, UploadConstants.QUALITY_AUTO_GOOD,
                        UploadConstants.CLOUDINARY_FETCH_FORMAT, UploadConstants.FORMAT_AUTO
                    )
                ),
                UploadConstants.CLOUDINARY_EAGER, Arrays.asList(
                    // Generate responsive sizes eagerly
                    ObjectUtils.asMap(UploadConstants.CLOUDINARY_WIDTH, UploadConstants.THUMBNAIL_WIDTH, UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.THUMBNAIL_HEIGHT, UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_FILL),
                    ObjectUtils.asMap(UploadConstants.CLOUDINARY_WIDTH, UploadConstants.MEDIUM_WIDTH, UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.MEDIUM_HEIGHT, UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_LIMIT),
                    ObjectUtils.asMap(UploadConstants.CLOUDINARY_WIDTH, UploadConstants.LARGE_WIDTH, UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.LARGE_HEIGHT, UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_LIMIT)
                )
            );
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            
            // Build image details with responsive sizes
            UploadDto.ImageDetails imageDetails = buildImageDetails(uploadResult, caption, alt, publicId);
            UploadDto.ImageData imageData = new UploadDto.ImageData(imageDetails);
            
            logger.info("✅ Image uploaded successfully: {}", publicId);
            return new UploadDto.ImageResponse(true, ApiConstants.MSG_IMAGE_UPLOADED, imageData);
            
        } catch (Exception e) {
            logger.error("❌ Image upload failed: {}", e.getMessage(), e);
            throw new RuntimeException(String.format(ErrorConstants.MSG_IMAGE_UPLOAD_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Upload a single video to Cloudinary
     */
    @SuppressWarnings("unchecked")
    public UploadDto.VideoResponse uploadVideo(MultipartFile file, String caption) {
        logger.info("🎥 Starting video upload: {}", file.getOriginalFilename());
        
        try {
            // Validate video file
            validateVideoFile(file);
            
            // Generate unique public ID
            String publicId = generateVideoPublicId();
            
            // Upload to Cloudinary with video transformations
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                UploadConstants.CLOUDINARY_FOLDER, UploadConstants.FOLDER_WILDLIFE_VIDEOS,
                UploadConstants.CLOUDINARY_PUBLIC_ID, publicId,
                UploadConstants.CLOUDINARY_RESOURCE_TYPE, UploadConstants.RESOURCE_TYPE_VIDEO,
                UploadConstants.CLOUDINARY_EAGER, Arrays.asList(
                    ObjectUtils.asMap(
                        UploadConstants.CLOUDINARY_WIDTH, UploadConstants.VIDEO_HD_WIDTH,
                        UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.VIDEO_HD_HEIGHT,
                        UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_LIMIT,
                        UploadConstants.CLOUDINARY_QUALITY, UploadConstants.QUALITY_AUTO_GOOD,
                        UploadConstants.CLOUDINARY_VIDEO_CODEC, UploadConstants.VIDEO_CODEC_H264
                    )
                ),
                UploadConstants.CLOUDINARY_EAGER_ASYNC, true // Process transformations asynchronously
            );
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            
            // Build video details with thumbnail
            UploadDto.VideoDetails videoDetails = buildVideoDetails(uploadResult, caption, publicId);
            UploadDto.VideoData videoData = new UploadDto.VideoData(videoDetails);
            
            logger.info("✅ Video uploaded successfully: {}", publicId);
            return new UploadDto.VideoResponse(true, ApiConstants.MSG_VIDEO_UPLOADED, videoData);
            
        } catch (Exception e) {
            logger.error("❌ Video upload failed: {}", e.getMessage(), e);
            throw new RuntimeException(String.format(ErrorConstants.MSG_VIDEO_UPLOAD_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Upload multiple images to Cloudinary
     */
    @SuppressWarnings("unchecked")
    public UploadDto.MultipleImagesResponse uploadMultipleImages(List<MultipartFile> files) {
        logger.info("🖼️📦 Starting multiple images upload: {} files", files.size());
        
        try {
            // Validate multiple images
            validateMultipleImages(files);
            
            List<UploadDto.ImageDetails> uploadedImages = new ArrayList<>();
            
            for (MultipartFile file : files) {
                String publicId = generateImagePublicId();
                
                Map<String, Object> uploadParams = ObjectUtils.asMap(
                    UploadConstants.CLOUDINARY_FOLDER, UploadConstants.FOLDER_WILDLIFE_IMAGES,
                    UploadConstants.CLOUDINARY_PUBLIC_ID, publicId,
                    UploadConstants.CLOUDINARY_TRANSFORMATION, Arrays.asList(
                        ObjectUtils.asMap(
                            UploadConstants.CLOUDINARY_WIDTH, UploadConstants.LARGE_WIDTH,
                            UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.LARGE_HEIGHT,
                            UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_LIMIT,
                            UploadConstants.CLOUDINARY_QUALITY, UploadConstants.QUALITY_AUTO_GOOD,
                            UploadConstants.CLOUDINARY_FETCH_FORMAT, UploadConstants.FORMAT_AUTO
                        )
                    ),
                    UploadConstants.CLOUDINARY_EAGER, Arrays.asList(
                        ObjectUtils.asMap(UploadConstants.CLOUDINARY_WIDTH, UploadConstants.THUMBNAIL_WIDTH, UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.THUMBNAIL_HEIGHT, UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_FILL),
                        ObjectUtils.asMap(UploadConstants.CLOUDINARY_WIDTH, UploadConstants.MEDIUM_WIDTH, UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.MEDIUM_HEIGHT, UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_LIMIT),
                        ObjectUtils.asMap(UploadConstants.CLOUDINARY_WIDTH, UploadConstants.LARGE_WIDTH, UploadConstants.CLOUDINARY_HEIGHT, UploadConstants.LARGE_HEIGHT, UploadConstants.CLOUDINARY_CROP, UploadConstants.CROP_LIMIT)
                    )
                );
                
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
                UploadDto.ImageDetails imageDetails = buildImageDetails(uploadResult, ApiConstants.DEFAULT_EMPTY_STRING, ApiConstants.DEFAULT_EMPTY_STRING, publicId);
                uploadedImages.add(imageDetails);
            }
            
            UploadDto.MultipleImagesData multipleImagesData = new UploadDto.MultipleImagesData(uploadedImages);
            String message = String.format(ApiConstants.MSG_IMAGES_UPLOADED_TEMPLATE, uploadedImages.size());
            
            logger.info("✅ Multiple images uploaded successfully: {} files", uploadedImages.size());
            return new UploadDto.MultipleImagesResponse(true, message, multipleImagesData);
            
        } catch (Exception e) {
            logger.error("❌ Multiple images upload failed: {}", e.getMessage(), e);
            throw new RuntimeException(String.format(ErrorConstants.MSG_IMAGES_UPLOAD_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Delete a file from Cloudinary
     */
    @SuppressWarnings("unchecked")
    public UploadDto.DeleteResponse deleteFile(String publicId, String resourceType) {
        logger.info("🗑️ Deleting file: {} (type: {})", publicId, resourceType);
        
        try {
            Map<String, Object> deleteParams = ObjectUtils.asMap(UploadConstants.CLOUDINARY_RESOURCE_TYPE, resourceType);
            Map<String, Object> result = cloudinary.uploader().destroy(publicId, deleteParams);
            
            String resultStatus = (String) result.get(UploadConstants.CLOUDINARY_RESULT);
            
            if (ErrorConstants.CLOUDINARY_SUCCESS_RESULT.equals(resultStatus)) {
                logger.info("✅ File deleted successfully: {}", publicId);
                return new UploadDto.DeleteResponse(true, ApiConstants.MSG_FILE_DELETED);
            } else {
                logger.warn("⚠️ File not found or already deleted: {}", publicId);
                throw new ResourceNotFoundException(ErrorConstants.MSG_FILE_NOT_FOUND_OR_DELETED);
            }
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("❌ Delete file failed: {}", e.getMessage(), e);
            throw new RuntimeException(String.format(ErrorConstants.MSG_DELETE_FILE_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Get transformed image URL
     */
    public UploadDto.TransformResponse transformImage(String publicId, UploadDto.TransformRequest request) {
        logger.info("🔄 Transforming image: {} with params: {}x{}", publicId, request.getWidth(), request.getHeight());
        
        try {
            // Generate transformed URL
            String transformedUrl = cloudinary.url()
                .transformation(new com.cloudinary.Transformation<>()
                    .width(request.getWidth())
                    .height(request.getHeight())
                    .crop(request.getCrop())
                    .quality(request.getQuality())
                    .fetchFormat("auto"))
                .secure(true)
                .generate(publicId);
            
            // Build transformation data
            Map<String, Object> transformation = Map.of(
                UploadConstants.CLOUDINARY_WIDTH, request.getWidth(),
                UploadConstants.CLOUDINARY_HEIGHT, request.getHeight(),
                UploadConstants.CLOUDINARY_CROP, request.getCrop(),
                UploadConstants.CLOUDINARY_QUALITY, request.getQuality()
            );
            
            UploadDto.TransformData transformData = new UploadDto.TransformData(transformedUrl, transformation);
            
            logger.info("✅ Image transformation successful: {}", publicId);
            return new UploadDto.TransformResponse(true, transformData);
            
        } catch (Exception e) {
            logger.error("❌ Image transformation failed: {}", e.getMessage(), e);
            throw new RuntimeException(String.format(ErrorConstants.MSG_TRANSFORM_IMAGE_FAILED, e.getMessage()), e);
        }
    }

    // Private helper methods

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(ErrorConstants.MSG_NO_IMAGE_FILE_PROVIDED);
        }
        
        if (file.getSize() > UploadConstants.MAX_IMAGE_SIZE_BYTES) {
            throw new IllegalArgumentException(ErrorConstants.MSG_IMAGE_SIZE_EXCEEDED);
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !UploadConstants.ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(ErrorConstants.MSG_INVALID_IMAGE_TYPE);
        }
    }

    private void validateVideoFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException(ErrorConstants.MSG_NO_VIDEO_FILE_PROVIDED);
        }
        
        if (file.getSize() > UploadConstants.MAX_VIDEO_SIZE_BYTES) {
            throw new IllegalArgumentException(ErrorConstants.MSG_VIDEO_SIZE_EXCEEDED);
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !UploadConstants.ALLOWED_VIDEO_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException(ErrorConstants.MSG_INVALID_VIDEO_TYPE);
        }
    }

    private void validateMultipleImages(List<MultipartFile> files) {
        if (files.isEmpty()) {
            throw new IllegalArgumentException(ErrorConstants.MSG_NO_IMAGE_FILES_PROVIDED);
        }
        
        if (files.size() > UploadConstants.MAX_MULTIPLE_IMAGES) {
            throw new IllegalArgumentException(String.format(ErrorConstants.MSG_TOO_MANY_FILES, UploadConstants.MAX_MULTIPLE_IMAGES));
        }
        
        for (MultipartFile file : files) {
            validateImageFile(file);
        }
    }

    private String generateImagePublicId() {
        long timestamp = System.currentTimeMillis();
        String random = UUID.randomUUID().toString().replaceAll("-", "").substring(0, UploadConstants.RANDOM_ID_LENGTH);
        return UploadConstants.IMAGE_PREFIX + timestamp + "_" + random;
    }

    private String generateVideoPublicId() {
        long timestamp = System.currentTimeMillis();
        String random = UUID.randomUUID().toString().replaceAll("-", "").substring(0, UploadConstants.RANDOM_ID_LENGTH);
        return UploadConstants.VIDEO_PREFIX + timestamp + "_" + random;
    }

    private UploadDto.ImageDetails buildImageDetails(Map<String, Object> uploadResult, String caption, String alt, String publicId) {
        UploadDto.ImageDetails imageDetails = new UploadDto.ImageDetails();
        imageDetails.setId(publicId);
        imageDetails.setUrl((String) uploadResult.get(UploadConstants.CLOUDINARY_SECURE_URL));
        imageDetails.setCaption(caption != null ? caption : ApiConstants.DEFAULT_EMPTY_STRING);
        imageDetails.setAlt(alt != null ? alt : ApiConstants.DEFAULT_EMPTY_STRING);
        
        // Build responsive sizes
        UploadDto.ImageSizes sizes = new UploadDto.ImageSizes();
        sizes.setThumbnail(generateImageUrl(publicId, UploadConstants.THUMBNAIL_WIDTH, UploadConstants.THUMBNAIL_HEIGHT, UploadConstants.CROP_FILL));
        sizes.setMedium(generateImageUrl(publicId, UploadConstants.MEDIUM_WIDTH, UploadConstants.MEDIUM_HEIGHT, UploadConstants.CROP_LIMIT));
        sizes.setLarge(generateImageUrl(publicId, UploadConstants.LARGE_WIDTH, UploadConstants.LARGE_HEIGHT, UploadConstants.CROP_LIMIT));
        sizes.setOriginal((String) uploadResult.get(UploadConstants.CLOUDINARY_SECURE_URL));
        
        imageDetails.setSizes(sizes);
        return imageDetails;
    }

    private UploadDto.VideoDetails buildVideoDetails(Map<String, Object> uploadResult, String caption, String publicId) {
        UploadDto.VideoDetails videoDetails = new UploadDto.VideoDetails();
        videoDetails.setId(publicId);
        videoDetails.setUrl((String) uploadResult.get(UploadConstants.CLOUDINARY_SECURE_URL));
        videoDetails.setCaption(caption != null ? caption : ApiConstants.DEFAULT_EMPTY_STRING);
        
        // Generate video thumbnail
        String thumbnail = cloudinary.url()
            .resourceType(UploadConstants.RESOURCE_TYPE_VIDEO)
            .format(UploadConstants.IMAGE_FORMAT_JPG)
            .transformation(new com.cloudinary.Transformation<>()
                .width(UploadConstants.VIDEO_THUMBNAIL_WIDTH)
                .height(UploadConstants.VIDEO_THUMBNAIL_HEIGHT)
                .crop(UploadConstants.CROP_FILL)
                .quality(UploadConstants.QUALITY_AUTO_GOOD))
            .secure(true)
            .generate(publicId);
        videoDetails.setThumbnail(thumbnail);
        
        // Extract duration and format if available
        if (uploadResult.get(UploadConstants.CLOUDINARY_DURATION) != null) {
            videoDetails.setDuration(((Number) uploadResult.get(UploadConstants.CLOUDINARY_DURATION)).doubleValue());
        }
        if (uploadResult.get(UploadConstants.CLOUDINARY_FORMAT) != null) {
            videoDetails.setFormat((String) uploadResult.get(UploadConstants.CLOUDINARY_FORMAT));
        }
        
        return videoDetails;
    }

    private String generateImageUrl(String publicId, int width, int height, String crop) {
        return cloudinary.url()
            .transformation(new com.cloudinary.Transformation<>()
                .width(width)
                .height(height)
                .crop(crop)
                .quality(UploadConstants.QUALITY_AUTO_GOOD)
                .fetchFormat(UploadConstants.FORMAT_AUTO))
            .secure(true)
            .generate(publicId);
    }
} 