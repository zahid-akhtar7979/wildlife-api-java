package com.wildlife.auth.api;

import com.wildlife.auth.dto.LoginRequest;
import com.wildlife.auth.dto.LoginResponse;
import com.wildlife.auth.dto.RegisterRequest;
import com.wildlife.auth.dto.StandardResponse;
import com.wildlife.auth.service.AuthService;
import com.wildlife.user.api.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller
 * Handles user authentication, registration, and profile management
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email and password")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "403", description = "Account not approved or disabled")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authService.login(loginRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            // Return 401 for authentication failures, 403 for account status issues
            if (response.getMessage().contains("pending") || response.getMessage().contains("disabled")) {
                return ResponseEntity.status(403).body(response);
            } else {
                return ResponseEntity.status(401).body(response);
            }
        }
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Validation errors or user already exists")
    public ResponseEntity<StandardResponse<UserDto>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        StandardResponse<UserDto> response = authService.register(registerRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(201).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Create first admin user - BOOTSTRAP ONLY
     * This endpoint should be removed in production
     */
    @PostMapping("/create-admin")
    @Operation(summary = "Create admin user", description = "Bootstrap endpoint to create the first admin user")
    @ApiResponse(responseCode = "201", description = "Admin user created successfully")
    @ApiResponse(responseCode = "400", description = "Admin already exists or validation errors")
    public ResponseEntity<StandardResponse<UserDto>> createAdmin(@Valid @RequestBody RegisterRequest registerRequest) {
        StandardResponse<UserDto> response = authService.createAdmin(registerRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(201).body(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Bootstrap endpoint to approve a user - TESTING ONLY
     */
    @PostMapping("/approve-user/{email}")
    @Operation(summary = "Approve user", description = "Bootstrap endpoint to approve users for testing")
    @ApiResponse(responseCode = "200", description = "User approved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<StandardResponse<UserDto>> approveUser(@PathVariable String email) {
        StandardResponse<UserDto> response = authService.approveUser(email);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get current user profile", 
        description = "Retrieve the profile of the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "User profile retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<StandardResponse<UserDto>> getCurrentUser() {
        StandardResponse<UserDto> response = authService.getCurrentUser();
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(response);
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/update-profile")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Update user profile", 
        description = "Update the profile of the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Profile updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation errors or email already exists")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<StandardResponse<UserDto>> updateProfile(@Valid @RequestBody UserDto updateRequest) {
        StandardResponse<UserDto> response = authService.updateProfile(updateRequest);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Change user password
     */
    @PutMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Change user password", 
        description = "Change the password of the authenticated user",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @ApiResponse(responseCode = "400", description = "Invalid current password or validation errors")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    public ResponseEntity<StandardResponse<String>> changePassword(@RequestBody ChangePasswordRequest request) {
        StandardResponse<String> response = authService.changePassword(request.getCurrentPassword(), request.getNewPassword());
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Change password request DTO
     */
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public ChangePasswordRequest() {}

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }
} 