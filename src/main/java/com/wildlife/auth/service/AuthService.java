package com.wildlife.auth.service;

import com.wildlife.auth.dto.LoginRequest;
import com.wildlife.auth.dto.LoginResponse;
import com.wildlife.auth.dto.RegisterRequest;
import com.wildlife.auth.dto.StandardResponse;
import com.wildlife.shared.security.JwtTokenProvider;
import com.wildlife.user.api.UserDto;
import com.wildlife.user.core.Role;
import com.wildlife.user.core.User;
import com.wildlife.user.persistence.UserRepository;
import com.wildlife.user.persistence.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Authentication service for user login, registration, and JWT operations
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthService(UserRepository userRepository, 
                      UserMapper userMapper,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Authenticate user and generate JWT token
     */
    public LoginResponse login(LoginRequest loginRequest) {
        logger.info("Attempting login for email: {}", loginRequest.getEmail());

        try {
            // Find user by email
            Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
            if (userOptional.isEmpty()) {
                logger.warn("Login failed: User not found for email: {}", loginRequest.getEmail());
                return LoginResponse.failure("Invalid credentials");
            }

            User user = userOptional.get();

            // Check password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.warn("Login failed: Invalid password for email: {}", loginRequest.getEmail());
                return LoginResponse.failure("Invalid credentials");
            }

            // Check if user is approved
            if (!user.getApproved()) {
                logger.warn("Login failed: User not approved for email: {}", loginRequest.getEmail());
                return LoginResponse.failure("Account pending admin approval");
            }

            // Check if user is enabled
            if (!user.getEnabled()) {
                logger.warn("Login failed: User disabled for email: {}", loginRequest.getEmail());
                return LoginResponse.failure("Account disabled");
            }

            // Generate JWT token
            String token = jwtTokenProvider.generateTokenForUserId(
                user.getId(), 
                user.getEmail(), 
                user.getName(),
                java.util.List.of("ROLE_" + user.getRole().toString())
            );

            // Convert to DTO
            UserDto userDto = userMapper.toDto(user);

            logger.info("Login successful for email: {}", loginRequest.getEmail());
            return LoginResponse.success(token, userDto);

        } catch (Exception e) {
            logger.error("Login error for email: {}", loginRequest.getEmail(), e);
            return LoginResponse.failure("Authentication failed. Please try again.");
        }
    }

    /**
     * Register new user
     */
    public StandardResponse<UserDto> register(RegisterRequest registerRequest) {
        logger.info("Attempting registration for email: {}", registerRequest.getEmail());

        try {
            // Check if user already exists
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                logger.warn("Registration failed: User already exists for email: {}", registerRequest.getEmail());
                return StandardResponse.failure("User already exists with this email");
            }

            // Create new user
            User user = new User();
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(Role.CONTRIBUTOR);
            user.setApproved(false); // Requires admin approval
            user.setEnabled(true);

            // Save user
            User savedUser = userRepository.save(user);

            // Convert to DTO
            UserDto userDto = userMapper.toDto(savedUser);

            logger.info("Registration successful for email: {}", registerRequest.getEmail());
            return StandardResponse.success("User registered successfully. Waiting for admin approval.", userDto);

        } catch (Exception e) {
            logger.error("Registration error for email: {}", registerRequest.getEmail(), e);
            return StandardResponse.failure("Server error during registration");
        }
    }

    /**
     * Create admin user - Bootstrap method for creating the first admin
     */
    public StandardResponse<UserDto> createAdmin(RegisterRequest registerRequest) {
        logger.info("Attempting admin creation for email: {}", registerRequest.getEmail());

        try {
            // Check if user already exists
            if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
                logger.warn("Admin creation failed: User already exists for email: {}", registerRequest.getEmail());
                return StandardResponse.failure("User already exists with this email");
            }

            // Check if any admin already exists
            if (userRepository.existsByRole(Role.ADMIN)) {
                logger.warn("Admin creation failed: Admin user already exists");
                return StandardResponse.failure("Admin user already exists in the system");
            }

            // Create admin user
            User user = new User();
            user.setName(registerRequest.getName());
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setRole(Role.ADMIN);
            user.setApproved(true); // Admin is pre-approved
            user.setEnabled(true);

            // Save user
            User savedUser = userRepository.save(user);

            // Convert to DTO
            UserDto userDto = userMapper.toDto(savedUser);

            logger.info("Admin creation successful for email: {}", registerRequest.getEmail());
            return StandardResponse.success("Admin user created successfully.", userDto);

        } catch (Exception e) {
            logger.error("Admin creation error for email: {}", registerRequest.getEmail(), e);
            return StandardResponse.failure("Server error during admin creation");
        }
    }

    /**
     * Get current authenticated user
     */
    public StandardResponse<UserDto> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return StandardResponse.failure("User not authenticated");
            }

            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                logger.warn("Current user not found for email: {}", email);
                return StandardResponse.failure("User not found");
            }

            User user = userOptional.get();
            UserDto userDto = userMapper.toDto(user);

            return StandardResponse.success(userDto);

        } catch (Exception e) {
            logger.error("Error getting current user", e);
            return StandardResponse.failure("Error retrieving user profile");
        }
    }

    /**
     * Update user profile
     */
    public StandardResponse<UserDto> updateProfile(UserDto updateRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return StandardResponse.failure("User not authenticated");
            }

            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                return StandardResponse.failure("User not found");
            }

            User user = userOptional.get();
            
            // Update allowed fields
            if (updateRequest.getName() != null && !updateRequest.getName().trim().isEmpty()) {
                user.setName(updateRequest.getName().trim());
            }
            
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
                // Check if new email already exists (for other users)
                Optional<User> existingUser = userRepository.findByEmail(updateRequest.getEmail());
                if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
                    return StandardResponse.failure("Email already exists");
                }
                user.setEmail(updateRequest.getEmail().trim());
            }

            // Save updated user
            User savedUser = userRepository.save(user);
            UserDto userDto = userMapper.toDto(savedUser);

            logger.info("Profile updated for user: {}", user.getEmail());
            return StandardResponse.success("Profile updated successfully", userDto);

        } catch (Exception e) {
            logger.error("Error updating user profile", e);
            return StandardResponse.failure("Error updating profile");
        }
    }

    /**
     * Change user password
     */
    public StandardResponse<String> changePassword(String currentPassword, String newPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return StandardResponse.failure("User not authenticated");
            }

            String email = authentication.getName();
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                return StandardResponse.failure("User not found");
            }

            User user = userOptional.get();

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                logger.warn("Password change failed: Invalid current password for user: {}", email);
                return StandardResponse.failure("Current password is incorrect");
            }

            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            logger.info("Password changed successfully for user: {}", email);
            return StandardResponse.success("Password changed successfully", null);

        } catch (Exception e) {
            logger.error("Error changing password", e);
            return StandardResponse.failure("Error changing password");
        }
    }

    /**
     * Approve user - Bootstrap method for testing
     */
    public StandardResponse<UserDto> approveUser(String email) {
        logger.info("Attempting to approve user: {}", email);

        try {
            Optional<User> userOptional = userRepository.findByEmail(email);
            
            if (userOptional.isEmpty()) {
                logger.warn("User approval failed: User not found for email: {}", email);
                return StandardResponse.failure("User not found");
            }

            User user = userOptional.get();
            user.setApproved(true);
            user.setEnabled(true);

            // Save updated user
            User savedUser = userRepository.save(user);
            UserDto userDto = userMapper.toDto(savedUser);

            logger.info("User approved successfully: {}", email);
            return StandardResponse.success("User approved successfully", userDto);

        } catch (Exception e) {
            logger.error("Error approving user: {}", email, e);
            return StandardResponse.failure("Error approving user");
        }
    }
} 