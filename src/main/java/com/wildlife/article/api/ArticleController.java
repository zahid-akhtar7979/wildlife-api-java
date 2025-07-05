package com.wildlife.article.api;

import com.wildlife.article.service.ArticleService;
import com.wildlife.shared.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Article Controller Implementation
 * 
 * Implements the ArticleApi interface contract for wildlife conservation article management.
 * This follows enterprise patterns used by tech giants like Google, Amazon, and Netflix
 * where controllers implement interface contracts for better:
 * 
 * - Testability (easy to mock interfaces)
 * - API contract clarity
 * - Multiple implementations support
 * - Client SDK generation
 * - Team collaboration (contract-first development)
 * 
 * @author Wildlife Team
 * @version 1.0.0
 */
@RestController
public class ArticleController implements ArticleApi {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * Convert 1-based pagination from frontend to 0-based pagination for Spring Data JPA
     */
    private Pageable convertPagination(Integer page, Integer size) {
        int pageNumber = (page != null && page > 0) ? page - 1 : 0; // Convert 1-based to 0-based
        int pageSize = (size != null && size > 0) ? size : 10; // Default to 10 if not specified
        return PageRequest.of(pageNumber, pageSize);
    }

    @Override
    public ResponseEntity<Page<ArticleDto>> getPublishedArticles(
            String search,
            String category,
            Boolean featured,
            List<String> tags,
            @PageableDefault(size = 10) Pageable pageable) {
        
        Page<ArticleDto> articles = articleService.getPublishedArticles(search, category, featured, tags, pageable);
        return ResponseEntity.ok(articles);
    }

    /**
     * Main articles endpoint with frontend-compatible response format
     * This handles the pagination conversion from 1-based (frontend) to 0-based (Spring Data JPA)
     * Returns: { "data": { "articles": [...], "pagination": {...} } }
     */
    @GetMapping
    public ResponseEntity<ApiResponse.ArticleDataResponse<ArticleDto>> getPublishedArticlesForFrontend(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer limit) {
        
        // Use 'limit' if provided, otherwise use 'size' - for frontend compatibility
        int pageSize = (limit != null && limit > 0) ? limit : (size != null && size > 0) ? size : 10;
        
        // Convert 1-based pagination from frontend to 0-based for Spring Data JPA
        Pageable pageable = convertPagination(page, pageSize);
        Page<ArticleDto> articles = articleService.getPublishedArticles(search, category, featured, tags, pageable);
        
        // Return in frontend-compatible format
        return ResponseEntity.ok(ApiResponse.ArticleDataResponse.fromPage(articles));
    }

    /**
     * Frontend-compatible endpoint for getting article by ID
     * Returns: { "data": { "article": {...} } }
     * This overrides the interface method to provide the frontend-expected format
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Object> getArticleById(@PathVariable Long id) {
        ArticleDto article = articleService.getArticleById(id);
        
        // Create the response in the format the frontend expects
        ApiResponse.SingleArticleDataResponse<ArticleDto> wrappedResponse = 
            ApiResponse.SingleArticleDataResponse.fromArticle(article);
        
        return ResponseEntity.ok(wrappedResponse);
    }

    @Override
    @GetMapping("/featured")
    public ResponseEntity<List<ArticleDto>> getFeaturedArticles(@RequestParam(defaultValue = "6") int limit) {
        List<ArticleDto> articles = articleService.getFeaturedArticles(limit);
        return ResponseEntity.ok(articles);
    }

    @Override
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse.CategoriesDataResponse> getCategories() {
        List<String> categories = articleService.getAllCategories();
        ApiResponse.CategoriesDataResponse response = ApiResponse.CategoriesDataResponse.fromCategories(categories);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/tags")
    public ResponseEntity<ApiResponse.TagsDataResponse> getTags() {
        List<String> tags = articleService.getAllTags();
        ApiResponse.TagsDataResponse response = ApiResponse.TagsDataResponse.fromTags(tags);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse.ArticleDataResponse<ArticleDto>> getArticlesByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // Convert 1-based pagination from frontend to 0-based for Spring Data JPA
        Pageable pageable = convertPagination(page, size);
        Page<ArticleDto> articles = articleService.getArticlesByCategory(category, pageable);
        
        // Return in frontend-compatible format
        return ResponseEntity.ok(ApiResponse.ArticleDataResponse.fromPage(articles));
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<ApiResponse.ArticleDataResponse<ArticleDto>> searchArticles(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        // Convert 1-based pagination from frontend to 0-based for Spring Data JPA
        Pageable pageable = convertPagination(page, size);
        Page<ArticleDto> articles = articleService.searchArticles(q, pageable);
        
        // Return in frontend-compatible format
        return ResponseEntity.ok(ApiResponse.ArticleDataResponse.fromPage(articles));
    }

    @Override
    @GetMapping("/most-viewed")
    public ResponseEntity<Page<ArticleDto>> getMostViewedArticles(Pageable pageable) {
        Page<ArticleDto> articles = articleService.getMostViewedArticles(pageable);
        return ResponseEntity.ok(articles);
    }

    @Override
    @GetMapping("/recent")
    public ResponseEntity<List<ArticleDto>> getRecentArticles(@RequestParam(defaultValue = "7") int days, @RequestParam(defaultValue = "10") int limit) {
        List<ArticleDto> articles = articleService.getRecentArticles(days, limit);
        return ResponseEntity.ok(articles);
    }

    @Override
    @GetMapping("/{id}/related")
    public ResponseEntity<List<ArticleDto>> getRelatedArticles(@PathVariable Long id, @RequestParam(defaultValue = "5") int limit) {
        List<ArticleDto> articles = articleService.getRelatedArticles(id, limit);
        return ResponseEntity.ok(articles);
    }

    /**
     * Get articles by author with frontend-compatible response format
     * This handles the pagination conversion from 1-based (frontend) to 0-based (Spring Data JPA)
     * Returns: { "data": { "articles": [...], "pagination": {...} } }
     */
    @GetMapping("/author/{authorId}")
    public ResponseEntity<ApiResponse.ArticleDataResponse<ArticleDto>> getArticlesByAuthorForFrontend(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer limit) {
        
        // Use 'limit' if provided, otherwise use 'size' - for frontend compatibility
        int pageSize = (limit != null && limit > 0) ? limit : (size != null && size > 0) ? size : 10;
        
        // Convert 1-based pagination from frontend to 0-based for Spring Data JPA
        Pageable pageable = convertPagination(page, pageSize);
        Page<ArticleDto> articles = articleService.getArticlesByAuthor(authorId, pageable);
        
        // Return in frontend-compatible format
        return ResponseEntity.ok(ApiResponse.ArticleDataResponse.fromPage(articles));
    }

    /**
     * Interface implementation for getArticlesByAuthor - kept for interface compliance
     */
    @Override
    public ResponseEntity<Page<ArticleDto>> getArticlesByAuthor(Long authorId, Pageable pageable) {
        Page<ArticleDto> articles = articleService.getArticlesByAuthor(authorId, pageable);
        return ResponseEntity.ok(articles);
    }

    /**
     * Create article endpoint with frontend-compatible response format
     * Implements interface method with proper mapping
     */
    @Override
    @PostMapping
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    public ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody ArticleDto articleDto) {
        ArticleDto createdArticle = articleService.createArticle(articleDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    /**
     * Update article endpoint with frontend-compatible response format
     * Implements interface method with proper mapping
     */
    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    public ResponseEntity<ArticleDto> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleDto articleDto) {
        ArticleDto updatedArticle = articleService.updateArticle(id, articleDto);
        return ResponseEntity.ok(updatedArticle);
    }

    /**
     * Delete article endpoint
     * Implements interface method with proper mapping
     */
    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @GetMapping("/my-articles")
    @PreAuthorize("hasRole('CONTRIBUTOR') or hasRole('ADMIN')")
    public ResponseEntity<Page<ArticleDto>> getCurrentUserArticles(@PageableDefault(size = 10) Pageable pageable) {
        Page<ArticleDto> articles = articleService.getCurrentUserArticles(pageable);
        return ResponseEntity.ok(articles);
    }

    @Override
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ArticleService.ArticleStatsDto> getArticleStatistics() {
        ArticleService.ArticleStatsDto stats = articleService.getArticleStatistics();
        return ResponseEntity.ok(stats);
    }

    @Override
    @PatchMapping("/{id}/publish")
    public ResponseEntity<ApiResponse.SingleArticleDataResponse<ArticleDto>> publishArticle(@PathVariable Long id) {
        ArticleDto publishedArticle = articleService.publishArticle(id);
        ApiResponse.SingleArticleDataResponse<ArticleDto> response = 
            ApiResponse.SingleArticleDataResponse.fromArticle(publishedArticle);
        return ResponseEntity.ok(response);
    }
} 