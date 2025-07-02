package com.wildlife.user.api;

import com.wildlife.user.core.Role;
import com.wildlife.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User Controller Implementation
 * 
 * Implements the UserApi interface contract for user management operations.
 * This follows enterprise patterns where controllers implement interface contracts
 * for better testability, API clarity, and team collaboration.
 * 
 * @author Wildlife Team
 * @version 1.0.0
 */
@RestController
public class UserController implements UserApi {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> updateCurrentUserProfile(UserDto userDto) {
        UserDto updatedUser = userService.updateCurrentUserProfile(userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getAllUsers(@PageableDefault(size = 20) Pageable pageable) {
        Page<UserDto> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> searchUsers(String q, Pageable pageable) {
        Page<UserDto> users = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(users);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getUsersByRole(Role role, Pageable pageable) {
        Page<UserDto> users = userService.findByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserDto>> getUsersByApprovalStatus(Boolean approved, Pageable pageable) {
        Page<UserDto> users = userService.findByApprovalStatus(approved, pageable);
        return ResponseEntity.ok(users);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> approveUser(Long id) {
        UserDto user = userService.approveUser(id);
        return ResponseEntity.ok(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> disableUser(Long id) {
        UserDto user = userService.disableUser(id);
        return ResponseEntity.ok(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> enableUser(Long id) {
        UserDto user = userService.enableUser(id);
        return ResponseEntity.ok(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> changeUserRole(Long id, Role role) {
        UserDto user = userService.changeUserRole(id, role);
        return ResponseEntity.ok(user);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserService.UserStatsDto> getUserStatistics() {
        UserService.UserStatsDto stats = userService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getTopContributors(int limit) {
        List<UserDto> contributors = userService.getTopContributors(PageRequest.of(0, limit));
        return ResponseEntity.ok(contributors);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getRecentUsers(int days) {
        List<UserDto> users = userService.getRecentUsers(days);
        return ResponseEntity.ok(users);
    }
} 