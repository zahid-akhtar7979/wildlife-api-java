package com.wildlife.article.persistence;

import com.wildlife.article.core.Article;
import com.wildlife.user.core.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Article entity operations.
 * Provides standard CRUD operations and custom queries for article management.
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    /**
     * Find all published articles with pagination
     */
    Page<Article> findByPublishedTrueOrderByPublishDateDesc(Pageable pageable);

    /**
     * Find all published articles
     */
    List<Article> findByPublishedTrueOrderByPublishDateDesc();

    /**
     * Find featured articles
     */
    List<Article> findByPublishedTrueAndFeaturedTrueOrderByPublishDateDesc();

    /**
     * Find featured articles with pagination
     */
    Page<Article> findByPublishedTrueAndFeaturedTrueOrderByPublishDateDesc(Pageable pageable);

    /**
     * Find articles by author ID
     */
    Page<Article> findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * Find published articles by author ID
     */
    Page<Article> findByAuthorIdAndPublishedTrueOrderByPublishDateDesc(Long authorId, Pageable pageable);

    /**
     * Find draft articles by author ID
     */
    Page<Article> findByAuthorIdAndPublishedFalseOrderByCreatedAtDesc(Long authorId, Pageable pageable);

    /**
     * Find articles by category
     */
    Page<Article> findByPublishedTrueAndCategoryOrderByPublishDateDesc(String category, Pageable pageable);

    /**
     * Find articles containing any of the specified tags
     * Using EXISTS for better H2 compatibility
     * Temporarily commented out due to tags field being @Transient
     */
    /*
    @Query("SELECT a FROM Article a WHERE a.published = true AND EXISTS " +
           "(SELECT t FROM Article a2 JOIN a2.tags t WHERE a2 = a AND t IN :tags) " +
           "ORDER BY a.publishDate DESC")
    Page<Article> findByPublishedTrueAndTagsIn(@Param("tags") List<String> tags, Pageable pageable);
    */

    /**
     * Search published articles by title, content, or excerpt
     * Using native SQL to avoid JPQL validation issues with LOWER function
     */
    @Query(value = "SELECT * FROM articles a WHERE a.published = true AND " +
           "(LOWER(a.title) LIKE :searchPattern OR " +
           "LOWER(a.content) LIKE :searchPattern OR " +
           "LOWER(a.excerpt) LIKE :searchPattern) " +
           "ORDER BY a.publish_date DESC", 
           nativeQuery = true)
    Page<Article> searchPublishedArticles(@Param("searchPattern") String searchPattern, Pageable pageable);

    /**
     * Get published articles using native SQL to bypass entity mapping issues
     * Returns raw data that can be manually mapped to DTOs
     */
    @Query(value = "SELECT a.id, a.title, a.excerpt, a.category, a.published, a.featured, " +
                   "a.views, a.author_id, a.created_at, a.updated_at, a.publish_date, " +
                   "a.images, u.name as author_name, u.email as author_email " +
                   "FROM articles a " +
                   "LEFT JOIN users u ON a.author_id = u.id " +
                   "WHERE a.published = true " +
                   "ORDER BY a.publish_date DESC", 
           nativeQuery = true)
    List<Object[]> findPublishedArticlesNative(Pageable pageable);

    /**
     * Complex search with multiple filters
     * Using native SQL to avoid JPQL validation issues with LOWER function
     * Temporarily commented out due to tags field being @Transient
     */
    /*
    @Query(value = "SELECT DISTINCT a.* FROM articles a LEFT JOIN article_tags at ON a.id = at.article_id " +
           "WHERE a.published = true " +
           "AND (:searchTerm IS NULL OR " +
           "LOWER(a.title) LIKE :searchTerm OR " +
           "LOWER(a.content) LIKE :searchTerm OR " +
           "LOWER(a.excerpt) LIKE :searchTerm) " +
           "AND (:category IS NULL OR a.category = :category) " +
           "AND (:featured IS NULL OR a.featured = :featured) " +
           "AND (:tags IS NULL OR at.tag IN (:tags)) " +
           "ORDER BY a.publish_date DESC", 
           nativeQuery = true)
    Page<Article> searchWithFilters(@Param("searchTerm") String searchTerm,
                                   @Param("category") String category,
                                   @Param("featured") Boolean featured,
                                   @Param("tags") List<String> tags,
                                   Pageable pageable);
    */

    /**
     * Find recent articles
     */
    @Query("SELECT a FROM Article a WHERE a.published = true AND a.publishDate >= :since ORDER BY a.publishDate DESC")
    List<Article> findRecentArticles(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find most viewed articles
     */
    Page<Article> findByPublishedTrueOrderByViewsDescPublishDateDesc(Pageable pageable);

    /**
     * Get all unique categories
     */
    @Query("SELECT DISTINCT a.category FROM Article a WHERE a.published = true AND a.category IS NOT NULL ORDER BY a.category")
    List<String> findDistinctCategories();

    /**
     * Get all unique tags
     * Using native query for better H2 compatibility
     * Temporarily commented out due to tags field being @Transient
     */
    /*
    @Query(value = "SELECT DISTINCT tag FROM article_tags at " +
                   "JOIN articles a ON at.article_id = a.id " +
                   "WHERE a.published = true ORDER BY tag", nativeQuery = true)
    List<String> findDistinctTags();
    */

    /**
     * Count published articles
     */
    long countByPublishedTrue();

    /**
     * Count draft articles
     */
    long countByPublishedFalse();

    /**
     * Count articles by author ID
     */
    long countByAuthorId(Long authorId);

    /**
     * Count published articles by author ID
     */
    long countByAuthorIdAndPublishedTrue(Long authorId);

    /**
     * Count articles by category
     */
    long countByPublishedTrueAndCategory(String category);

    /**
     * Increment view count
     */
    @Modifying
    @Query("UPDATE Article a SET a.views = a.views + 1 WHERE a.id = :articleId")
    void incrementViews(@Param("articleId") Long articleId);

    /**
     * Find article by ID (simplified without author join)
     */
    Optional<Article> findById(Long articleId);

    /**
     * Find related articles by tags and category
     * Temporarily commented out due to tags field being @Transient
     */
    /*
    @Query("SELECT DISTINCT a FROM Article a LEFT JOIN a.tags t WHERE a.published = true AND a.id != :excludeId " +
           "AND (a.category = :category OR t IN :tags) " +
           "ORDER BY a.publishDate DESC")
    List<Article> findRelatedArticles(@Param("excludeId") Long excludeId,
                                     @Param("category") String category,
                                     @Param("tags") List<String> tags,
                                     Pageable pageable);
    */

    /**
     * Find articles published between dates
     */
    @Query("SELECT a FROM Article a WHERE a.published = true AND a.publishDate BETWEEN :startDate AND :endDate ORDER BY a.publishDate DESC")
    List<Article> findPublishedBetweenDates(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find top articles by views in a time period
     */
    @Query("SELECT a FROM Article a WHERE a.published = true AND a.publishDate >= :since ORDER BY a.views DESC, a.publishDate DESC")
    List<Article> findTopViewedSince(@Param("since") LocalDateTime since, Pageable pageable);
} 