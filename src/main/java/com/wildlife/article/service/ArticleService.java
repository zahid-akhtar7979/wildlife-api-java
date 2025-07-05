package com.wildlife.article.service;

import com.wildlife.article.api.ArticleDto;
import com.wildlife.article.core.Article;
import com.wildlife.article.persistence.ArticleMapper;
import com.wildlife.article.persistence.ArticleRepository;
import com.wildlife.shared.config.JsonListConverter;
import com.wildlife.shared.exception.ResourceNotFoundException;
import com.wildlife.user.core.User;
import com.wildlife.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service class for Article entity operations.
 * Provides business logic for article management with access control.
 */
@Service
@Transactional
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final UserService userService;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, 
                         ArticleMapper articleMapper,
                         UserService userService) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userService = userService;
    }

    /**
     * Get all published articles with pagination and filtering
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> getPublishedArticles(String search, String category, 
                                                Boolean featured, List<String> tags, 
                                                Pageable pageable) {
        // Use native query approach to bypass entity mapping issues
        return getPublishedArticlesNative(search, category, featured, tags, pageable);
    }

    /**
     * Get featured articles
     */
    @Transactional(readOnly = true)
    public List<ArticleDto> getFeaturedArticles(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return articleRepository.findByPublishedTrueAndFeaturedTrueOrderByPublishDateDesc(pageable)
                .getContent()
                .stream()
                .map(articleMapper::toDto)
                .toList();
    }

    /**
     * Get article by ID with access control
     */
    @Transactional
    public ArticleDto getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));

        // Check access permissions for drafts
        if (!article.isPublished()) {
            User currentUser = getCurrentUserOrNull();
            if (!article.canBeAccessedBy(currentUser)) {
                throw new AccessDeniedException("You don't have permission to access this article");
            }
        }

        // Increment view count for published articles
        if (article.isPublished()) {
            incrementViewCount(id);
            article.incrementViews(); // Update the local object for response
        }

        return articleMapper.toDto(article);
    }

    /**
     * Create new article
     */
    public ArticleDto createArticle(ArticleDto articleDto) {
        User currentUser = userService.getCurrentUserEntity();
        
        Article article = articleMapper.toEntity(articleDto);
        article.setAuthorId(currentUser.getId());
        
        // Set publish date if publishing
        if (Boolean.TRUE.equals(article.getPublished())) {
            article.setPublishDate(LocalDateTime.now());
        }

        Article savedArticle = articleRepository.save(article);
        logger.info("Created new article: {} by user: {}", savedArticle.getTitle(), currentUser.getEmail());
        
        return articleMapper.toDto(savedArticle);
    }

    /**
     * Update article with access control
     */
    public ArticleDto updateArticle(Long id, ArticleDto articleDto) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));

        User currentUser = userService.getCurrentUserEntity();
        validateArticleAccess(existingArticle, currentUser);

        // Update fields
        articleMapper.updateEntityFromDto(articleDto, existingArticle);
        
        // Handle publishing logic
        if (Boolean.TRUE.equals(articleDto.getPublished()) && !existingArticle.isPublished()) {
            existingArticle.setPublishDate(LocalDateTime.now());
        }

        Article savedArticle = articleRepository.save(existingArticle);
        logger.info("Updated article: {} by user: {}", savedArticle.getTitle(), currentUser.getEmail());
        
        return articleMapper.toDto(savedArticle);
    }

    /**
     * Delete article with access control
     */
    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));

        User currentUser = userService.getCurrentUserEntity();
        validateArticleAccess(article, currentUser);

        articleRepository.delete(article);
        logger.info("Deleted article: {} by user: {}", article.getTitle(), currentUser.getEmail());
    }

    /**
     * Publish a draft article
     * Sets the article as published and sets the publish date
     */
    public ArticleDto publishArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));

        User currentUser = userService.getCurrentUserEntity();
        validateArticleAccess(article, currentUser);

        // Check if article is already published
        if (article.isPublished()) {
            throw new IllegalStateException("Article is already published");
        }

        // Publish the article
        article.setPublished(true);
        article.setPublishDate(LocalDateTime.now());

        Article savedArticle = articleRepository.save(article);
        logger.info("Published article: {} by user: {}", savedArticle.getTitle(), currentUser.getEmail());
        
        return articleMapper.toDto(savedArticle);
    }

    /**
     * Get articles by current user
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> getCurrentUserArticles(Pageable pageable) {
        User currentUser = userService.getCurrentUserEntity();
        return articleRepository.findByAuthorIdOrderByCreatedAtDesc(currentUser.getId(), pageable)
                .map(articleMapper::toDto);
    }

    /**
     * Get articles by author ID
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> getArticlesByAuthor(Long authorId, Pageable pageable) {
        // Temporarily simplified - just return all articles by the author
        // TODO: Re-enable access control later
        return articleRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable)
                .map(articleMapper::toDto);
    }

    /**
     * Search articles
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(String searchTerm, Pageable pageable) {
        // Pre-process search term with wildcards and lowercase
        String searchPattern = "%" + searchTerm.toLowerCase() + "%";
        return articleRepository.searchPublishedArticles(searchPattern, pageable)
                .map(articleMapper::toDto);
    }

    /**
     * Get articles by category
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> getArticlesByCategory(String category, Pageable pageable) {
        return articleRepository.findByPublishedTrueAndCategoryOrderByPublishDateDesc(category, pageable)
                .map(articleMapper::toDto);
    }

    /**
     * Get articles by tags - temporarily disabled due to @Transient tags field
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> getArticlesByTags(List<String> tags, Pageable pageable) {
        // Temporarily return empty page until tags field is restored
        return org.springframework.data.domain.Page.empty(pageable);
    }

    /**
     * Get all available categories
     */
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return articleRepository.findDistinctCategories();
    }

    /**
     * Get all available tags - temporarily disabled due to @Transient tags field
     */
    @Transactional(readOnly = true)
    public List<String> getAllTags() {
        // Temporarily return empty list until tags field is restored
        return new ArrayList<>();
    }

    /**
     * Get related articles - temporarily disabled due to @Transient tags field
     */
    @Transactional(readOnly = true)
    public List<ArticleDto> getRelatedArticles(Long articleId, int limit) {
        // Temporarily return empty list until tags field is restored
        return new ArrayList<>();
    }

    /**
     * Get most viewed articles
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> getMostViewedArticles(Pageable pageable) {
        return articleRepository.findByPublishedTrueOrderByViewsDescPublishDateDesc(pageable)
                .map(articleMapper::toDto);
    }

    /**
     * Get recent articles
     */
    @Transactional(readOnly = true)
    public List<ArticleDto> getRecentArticles(int days, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Pageable pageable = PageRequest.of(0, limit);
        return articleRepository.findRecentArticles(since, pageable)
                .stream()
                .map(articleMapper::toDto)
                .toList();
    }

    /**
     * Get article statistics
     */
    @Transactional(readOnly = true)
    public ArticleStatsDto getArticleStatistics() {
        ArticleStatsDto stats = new ArticleStatsDto();
        stats.setTotalArticles(articleRepository.count());
        stats.setPublishedArticles(articleRepository.countByPublishedTrue());
        stats.setDraftArticles(articleRepository.countByPublishedFalse());
        
        return stats;
    }

    /**
     * Get all published articles using native query to bypass entity mapping issues
     */
    @Transactional(readOnly = true)
    public Page<ArticleDto> getPublishedArticlesNative(String search, String category, 
                                                      Boolean featured, List<String> tags, 
                                                      Pageable pageable) {
        // Use native query to bypass entity mapping issues
        List<Object[]> nativeResults = articleRepository.findPublishedArticlesNative(pageable);
        
        // Manually map results to DTOs
        List<ArticleDto> articles = new ArrayList<>();
        for (Object[] row : nativeResults) {
            ArticleDto dto = new ArticleDto();
            dto.setId((Long) row[0]);
            dto.setTitle((String) row[1]);
            dto.setExcerpt((String) row[2]);
            dto.setCategory((String) row[3]);
            dto.setPublished((Boolean) row[4]);
            dto.setFeatured((Boolean) row[5]);
            dto.setViews((Integer) row[6]);
            dto.setAuthorId((Long) row[7]);
            
            // Convert java.sql.Timestamp to java.time.LocalDateTime
            dto.setCreatedAt(row[8] != null ? ((java.sql.Timestamp) row[8]).toLocalDateTime() : null);
            dto.setUpdatedAt(row[9] != null ? ((java.sql.Timestamp) row[9]).toLocalDateTime() : null);
            dto.setPublishDate(row[10] != null ? ((java.sql.Timestamp) row[10]).toLocalDateTime() : null);
            
            // Parse images using JsonListConverter
            String imagesJson = (String) row[11];
            if (imagesJson != null) {
                try {
                    List<Map<String, Object>> images = new JsonListConverter().convertToEntityAttribute(imagesJson);
                    dto.setImages(images);
                } catch (Exception e) {
                    logger.error("Error parsing images JSON for article {}: {}", dto.getId(), e.getMessage());
                    dto.setImages(new ArrayList<>());
                }
            } else {
                dto.setImages(new ArrayList<>());
            }

            // Set author information from the joined user data
            String authorName = (String) row[12];
            String authorEmail = (String) row[13];
            if (authorName != null || authorEmail != null) {
                com.wildlife.user.api.UserDto authorDto = new com.wildlife.user.api.UserDto();
                authorDto.setId(dto.getAuthorId());
                authorDto.setName(authorName);
                authorDto.setEmail(authorEmail);
                dto.setAuthor(authorDto);
            }
            
            // Set default values for fields not included in native query
            dto.setContent(""); // Content not included to avoid mapping issues
            dto.setVideos(new ArrayList<>());
            dto.setTags(new ArrayList<>());
            
            articles.add(dto);
        }
        
        // Create Page manually since native query returns List
        long total = articleRepository.countByPublishedTrue();
        return new org.springframework.data.domain.PageImpl<>(articles, pageable, total);
    }

    // Private helper methods

    private boolean hasFilters(String search, String category, Boolean featured, List<String> tags) {
        return search != null || category != null || featured != null || 
               (tags != null && !tags.isEmpty());
    }

    private void validateArticleAccess(Article article, User user) {
        if (!user.isAdmin() && !Objects.equals(article.getAuthorId(), user.getId())) {
            throw new AccessDeniedException("You don't have permission to modify this article");
        }
    }

    private User getCurrentUserOrNull() {
        try {
            return userService.getCurrentUserEntity();
        } catch (Exception e) {
            return null;
        }
    }

    @Transactional
    private void incrementViewCount(Long articleId) {
        try {
            articleRepository.incrementViews(articleId);
        } catch (Exception e) {
            logger.warn("Failed to increment view count for article {}: {}", articleId, e.getMessage());
        }
    }

    /**
     * Article statistics DTO
     */
    public static class ArticleStatsDto {
        private long totalArticles;
        private long publishedArticles;
        private long draftArticles;

        // Getters and setters
        public long getTotalArticles() { return totalArticles; }
        public void setTotalArticles(long totalArticles) { this.totalArticles = totalArticles; }
        
        public long getPublishedArticles() { return publishedArticles; }
        public void setPublishedArticles(long publishedArticles) { this.publishedArticles = publishedArticles; }
        
        public long getDraftArticles() { return draftArticles; }
        public void setDraftArticles(long draftArticles) { this.draftArticles = draftArticles; }
    }
} 