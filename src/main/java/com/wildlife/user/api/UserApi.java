package com.wildlife.user.api;

import com.wildlife.user.core.Role;
import com.wildlife.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Management API Contract Interface
 * 
 * Defines the REST API contract for user management operations.
 * This interface follows enterprise patterns for clean API design,
 * contract-first development, and better testability.
 * 
 * Key Features:
 * - Role-based access control
 * - User profile management
 * - Administrative user operations
 * - Comprehensive user statistics
 * 
 * @author Wildlife Team
 * @version 1.0.0
 */
@Tag(name = "Users", description = "User management and profile operations")
@RequestMapping("/api/users")
public interface UserApi {

    // ==================== USER PROFILE ENDPOINTS ====================

    @Operation(summary = "Get current user profile", 
               description = "Retrieve the profile of the authenticated user",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @GetMapping("/me")
    ResponseEntity<UserDto> getCurrentUser();

    @Operation(summary = "Update current user profile", 
               description = "Update the profile of the authenticated user",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user data"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PutMapping("/me")
    ResponseEntity<UserDto> updateCurrentUserProfile(@Valid @RequestBody UserDto userDto);

    // ==================== ADMIN USER MANAGEMENT ====================

    @Operation(summary = "Get all users", 
               description = "Retrieve all users with pagination (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    @GetMapping
    ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable);

    @Operation(summary = "Search users", 
               description = "Search users by name or email (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/search")
    ResponseEntity<Page<UserDto>> searchUsers(
            @Parameter(description = "Search term for name or email", required = true)
            @RequestParam String q,
            Pageable pageable);

    @Operation(summary = "Get user by ID", 
               description = "Retrieve a specific user by ID (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<UserDto> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id);

    // ==================== USER FILTERING ====================

    @Operation(summary = "Get users by role", 
               description = "Retrieve users filtered by role (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/role/{role}")
    ResponseEntity<Page<UserDto>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable Role role,
            Pageable pageable);

    @Operation(summary = "Get users by approval status", 
               description = "Retrieve users filtered by approval status (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/approval/{approved}")
    ResponseEntity<Page<UserDto>> getUsersByApprovalStatus(
            @Parameter(description = "Approval status") @PathVariable Boolean approved,
            Pageable pageable);

    // ==================== USER STATE MANAGEMENT ====================

    @Operation(summary = "Approve user", 
               description = "Approve a user account (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User approved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/approve")
    ResponseEntity<UserDto> approveUser(
            @Parameter(description = "User ID") @PathVariable Long id);

    @Operation(summary = "Disable user", 
               description = "Disable a user account (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User disabled successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/disable")
    ResponseEntity<UserDto> disableUser(
            @Parameter(description = "User ID") @PathVariable Long id);

    @Operation(summary = "Enable user", 
               description = "Enable a user account (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User enabled successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/enable")
    ResponseEntity<UserDto> enableUser(
            @Parameter(description = "User ID") @PathVariable Long id);

    @Operation(summary = "Change user role", 
               description = "Change a user's role (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User role changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid role"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "403", description = "Admin access required"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/role")
    ResponseEntity<UserDto> changeUserRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "New role") @RequestParam Role role);

    // ==================== ANALYTICS AND REPORTING ====================

    @Operation(summary = "Get user statistics", 
               description = "Get statistics about users (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/statistics")
    ResponseEntity<UserService.UserStatsDto> getUserStatistics();

    @Operation(summary = "Get top contributors", 
               description = "Get users with most articles (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/top-contributors")
    ResponseEntity<List<UserDto>> getTopContributors(
            @Parameter(description = "Maximum number of contributors")
            @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "Get recent users", 
               description = "Get recently registered users (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/recent")
    ResponseEntity<List<UserDto>> getRecentUsers(
            @Parameter(description = "Number of days to look back")
            @RequestParam(defaultValue = "30") int days);
} 