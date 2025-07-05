package com.wildlife.user.service;

import com.wildlife.user.api.UserDto;
import com.wildlife.user.core.User;
import com.wildlife.user.core.Role;
import com.wildlife.shared.exception.ResourceNotFoundException;
import com.wildlife.shared.exception.UserAlreadyExistsException;
import com.wildlife.user.persistence.UserMapper;
import com.wildlife.user.persistence.UserRepository;
import com.wildlife.shared.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for User entity operations.
 * Provides business logic for user management with JWT authentication.
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Get current user information from JWT token
     */
    @Transactional(readOnly = true)
    public UserDto getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("No user ID found in token"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        return userMapper.toDto(user);
    }

    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return userMapper.toDto(user);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto);
    }

// Keycloak ID lookup removed - using JWT authentication

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDto);
    }

    /**
     * Search users by name or email
     */
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.searchByNameOrEmail(searchTerm, pageable)
                .map(userMapper::toDto);
    }

    /**
     * Find users by role
     */
    @Transactional(readOnly = true)
    public Page<UserDto> findByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(userMapper::toDto);
    }

    /**
     * Find users by approval status
     */
    @Transactional(readOnly = true)
    public Page<UserDto> findByApprovalStatus(Boolean approved, Pageable pageable) {
        return userRepository.findByApproved(approved, pageable)
                .map(userMapper::toDto);
    }

    /**
     * Update current user profile
     */
    public UserDto updateCurrentUserProfile(UserDto userDto) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("No user ID found in token"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Only allow updating certain fields
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        
        if (userDto.getEmail() != null && !userDto.getEmail().equals(user.getEmail())) {
            // Check if email is already taken
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new UserAlreadyExistsException("Email already exists: " + userDto.getEmail());
            }
            user.setEmail(userDto.getEmail());
        }

        // Handle bio update
        if (userDto.getBio() != null) {
            user.setBio(userDto.getBio());
        }

        // Handle profile picture URL update
        if (userDto.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(userDto.getProfilePictureUrl());
        }

        User savedUser = userRepository.save(user);
        logger.info("Updated profile for user ID: {}", savedUser.getId());
        
        return userMapper.toDto(savedUser);
    }

    /**
     * Approve user (Admin only)
     */
    public UserDto approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        user.setApproved(true);
        User savedUser = userRepository.save(user);
        
        logger.info("User approved: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    /**
     * Disable user (Admin only)
     */
    public UserDto disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        user.setEnabled(false);
        User savedUser = userRepository.save(user);
        
        logger.info("User disabled: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    /**
     * Enable user (Admin only)
     */
    public UserDto enableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        user.setEnabled(true);
        User savedUser = userRepository.save(user);
        
        logger.info("User enabled: {} (ID: {})", savedUser.getEmail(), savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    /**
     * Change user role (Admin only)
     */
    public UserDto changeUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Role oldRole = user.getRole();
        user.setRole(newRole);
        User savedUser = userRepository.save(user);
        
        logger.info("User role changed from {} to {}: {} (ID: {})", 
                   oldRole, newRole, savedUser.getEmail(), savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStatsDto getUserStatistics() {
        UserStatsDto stats = new UserStatsDto();
        stats.setTotalUsers(userRepository.count());
        stats.setApprovedUsers(userRepository.countByApprovedTrue());
        stats.setPendingApprovalUsers(userRepository.countByApprovedFalse());
        stats.setEnabledUsers(userRepository.countByEnabledTrue());
        stats.setAdminUsers(userRepository.countByRole(Role.ADMIN));
        stats.setContributorUsers(userRepository.countByRole(Role.CONTRIBUTOR));
        
        return stats;
    }

    /**
     * Get top contributors
     */
    @Transactional(readOnly = true)
    public List<UserDto> getTopContributors(Pageable pageable) {
        return userRepository.findTopContributors(pageable)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Get recent users
     */
    @Transactional(readOnly = true)
    public List<UserDto> getRecentUsers(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userRepository.findByCreatedAtAfter(since)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Get user entity by ID (internal use)
     */
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }

    /**
     * Get current user entity (internal use)
     */
    public User getCurrentUserEntity() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new IllegalStateException("No user ID found in token"));
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /**
     * User statistics DTO
     */
    public static class UserStatsDto {
        private long totalUsers;
        private long approvedUsers;
        private long pendingApprovalUsers;
        private long enabledUsers;
        private long adminUsers;
        private long contributorUsers;

        // Getters and setters
        public long getTotalUsers() { return totalUsers; }
        public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
        
        public long getApprovedUsers() { return approvedUsers; }
        public void setApprovedUsers(long approvedUsers) { this.approvedUsers = approvedUsers; }
        
        public long getPendingApprovalUsers() { return pendingApprovalUsers; }
        public void setPendingApprovalUsers(long pendingApprovalUsers) { this.pendingApprovalUsers = pendingApprovalUsers; }
        
        public long getEnabledUsers() { return enabledUsers; }
        public void setEnabledUsers(long enabledUsers) { this.enabledUsers = enabledUsers; }
        
        public long getAdminUsers() { return adminUsers; }
        public void setAdminUsers(long adminUsers) { this.adminUsers = adminUsers; }
        
        public long getContributorUsers() { return contributorUsers; }
        public void setContributorUsers(long contributorUsers) { this.contributorUsers = contributorUsers; }
    }
} 