package com.wildlife.upload.api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Upload API interface defining all file upload operations.
 * Handles image and video uploads with Cloudinary integration.
 */
@Tag(
    name = "Upload", 
    description = "File upload management for images and videos"
)
@SecurityRequirement(name = "bearerAuth")
public interface UploadApi {

    @Operation(
        summary = "Upload a single image",
        description = "Upload an image file to Cloudinary with automatic responsive size generation. " +
                     "Supports JPEG, PNG, WebP, and AVIF formats up to 10MB."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Image uploaded successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadDto.ImageResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid file or file too large"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Contributor or Admin role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during upload"
        )
    })
    ResponseEntity<UploadDto.ImageResponse> uploadImage(
        @Parameter(
            description = "Image file to upload",
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        MultipartFile image,
        
        @Parameter(
            description = "Image caption (optional)",
            example = "A beautiful wildlife photograph"
        )
        String caption,
        
        @Parameter(
            description = "Image alt text for accessibility (optional)",
            example = "Elephant in natural habitat"
        )
        String alt
    );

    @Operation(
        summary = "Upload a single video",
        description = "Upload a video file to Cloudinary with automatic thumbnail generation. " +
                     "Supports MP4, MOV, AVI, MKV, and WebM formats up to 100MB."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Video uploaded successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadDto.VideoResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid file or file too large"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Contributor or Admin role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during upload"
        )
    })
    ResponseEntity<UploadDto.VideoResponse> uploadVideo(
        @Parameter(
            description = "Video file to upload",
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        MultipartFile video,
        
        @Parameter(
            description = "Video caption (optional)",
            example = "Wildlife documentary footage"
        )
        String caption
    );

    @Operation(
        summary = "Upload multiple images",
        description = "Upload up to 10 images at once with automatic responsive size generation. " +
                     "Each image supports JPEG, PNG, WebP, and AVIF formats up to 10MB."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Images uploaded successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadDto.MultipleImagesResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid files, too many files, or files too large"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Contributor or Admin role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during upload"
        )
    })
    ResponseEntity<UploadDto.MultipleImagesResponse> uploadMultipleImages(
        @Parameter(
            description = "Array of image files to upload (max 10)",
            required = true,
            content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
        )
        List<MultipartFile> images
    );

    @Operation(
        summary = "Delete a file from Cloudinary",
        description = "Delete an uploaded file (image or video) from Cloudinary using its public ID."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "File deleted successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadDto.DeleteResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "File not found or already deleted"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Contributor or Admin role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during deletion"
        )
    })
    ResponseEntity<UploadDto.DeleteResponse> deleteFile(
        @Parameter(
            description = "Cloudinary public ID of the file to delete",
            required = true,
            example = "wildlife_1234567890_abc123"
        )
        String publicId,
        
        @Parameter(
            description = "Type of resource to delete",
            example = "image",
            schema = @Schema(allowableValues = {"image", "video"})
        )
        String resourceType
    );

    @Operation(
        summary = "Get transformed image URL",
        description = "Generate a transformed image URL with custom dimensions and quality settings."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Transformation successful",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadDto.TransformResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Invalid transformation parameters"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication required"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Insufficient permissions - Contributor or Admin role required"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Internal server error during transformation"
        )
    })
    ResponseEntity<UploadDto.TransformResponse> transformImage(
        @Parameter(
            description = "Cloudinary public ID of the image to transform",
            required = true,
            example = "wildlife_1234567890_abc123"
        )
        String publicId,
        
        @RequestBody(
            description = "Transformation parameters",
            required = true,
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = UploadDto.TransformRequest.class)
            )
        )
        UploadDto.TransformRequest transformRequest
    );
} 