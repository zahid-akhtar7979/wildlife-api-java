package com.wildlife.user.persistence;

import com.wildlife.user.core.User;
import com.wildlife.user.core.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Provides standard CRUD operations and custom queries for user management.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if any user exists with the specified role
     */
    boolean existsByRole(Role role);

    /**
     * Find all users by role
     */
    List<User> findByRole(Role role);

    /**
     * Find all users by role with pagination
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Find all approved users
     */
    List<User> findByApprovedTrue();

    /**
     * Find all pending approval users
     */
    List<User> findByApprovedFalse();

    /**
     * Find all enabled users
     */
    List<User> findByEnabledTrue();

    /**
     * Find all disabled users
     */
    List<User> findByEnabledFalse();

    /**
     * Find users by approval status with pagination
     */
    Page<User> findByApproved(Boolean approved, Pageable pageable);

    /**
     * Find users by enabled status with pagination
     */
    Page<User> findByEnabled(Boolean enabled, Pageable pageable);

    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Search users by name or email (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchByNameOrEmail(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") Role role);

    /**
     * Count approved users
     */
    long countByApprovedTrue();

    /**
     * Count pending approval users
     */
    long countByApprovedFalse();

    /**
     * Count enabled users
     */
    long countByEnabledTrue();

    /**
     * Find active contributors (approved and enabled contributors)
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.approved = true AND u.enabled = true")
    List<User> findActiveContributors(@Param("role") Role role);

    /**
     * Find users with articles count - temporarily using direct query without relationship
     */
    @Query("SELECT u FROM User u WHERE u.id = :userId")
    Optional<User> findByIdWithArticles(@Param("userId") Long userId);

    /**
     * Find top contributors by article count - using native SQL since relationship is commented out
     */
    @Query(value = "SELECT u.* FROM users u " +
           "JOIN articles a ON u.id = a.author_id " +
           "WHERE u.approved = true AND u.enabled = true " +
           "GROUP BY u.id ORDER BY COUNT(a.id) DESC", 
           nativeQuery = true)
    List<User> findTopContributors(Pageable pageable);
} 