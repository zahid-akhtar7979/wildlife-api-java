package com.wildlife.article.api;

import com.wildlife.article.service.ArticleService;
import com.wildlife.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Article API Contract Interface
 * 
 * Defines the REST API contract for wildlife conservation article management.
 * This interface separates the API contract from its implementation, following
 * enterprise patterns used by Google, Amazon, Netflix, and other tech giants.
 * 
 * Benefits:
 * - Contract-first development
 * - Better testability with mocks
 * - Multiple implementations support
 * - Clean API documentation
 * - Client SDK generation
 * - API versioning support
 * 
 * @author Wildlife Team
 * @version 1.0.0
 */
@Tag(name = "Articles", description = "Wildlife conservation article management API")
@RequestMapping("/api/articles")
public interface ArticleApi {

    // ==================== PUBLIC ENDPOINTS ====================

    @Operation(summary = "Get all published articles", 
               description = "Retrieve published articles with optional filtering and pagination")
    ResponseEntity<Page<ArticleDto>> getPublishedArticles(
            @Parameter(description = "Search term for title, content, or excerpt")
            @RequestParam(required = false) String search,
            
            @Parameter(description = "Filter by category")
            @RequestParam(required = false) String category,
            
            @Parameter(description = "Filter featured articles")
            @RequestParam(required = false) Boolean featured,
            
            @Parameter(description = "Filter by tags (comma-separated)")
            @RequestParam(required = false) List<String> tags,
            
            Pageable pageable);

    @Operation(summary = "Get article by ID", 
               description = "Retrieve a specific article by its ID. Drafts require authentication.")
    ResponseEntity<Object> getArticleById(
            @Parameter(description = "Article ID") Long id);

    @Operation(summary = "Get featured articles", 
               description = "Retrieve featured articles for homepage display")
    @GetMapping("/featured")
    ResponseEntity<List<ArticleDto>> getFeaturedArticles(
            @Parameter(description = "Maximum number of articles to return")
            @RequestParam(defaultValue = "6") int limit);

    /**
     * Get all available categories
     * 
     * @return List of category names
     */
    @GetMapping("/categories")
    ResponseEntity<ApiResponse.CategoriesDataResponse> getCategories();

    @Operation(summary = "Get available tags", 
               description = "Retrieve all available article tags")
    @GetMapping("/tags")
    ResponseEntity<ApiResponse.TagsDataResponse> getTags();

    @Operation(summary = "Get articles by category", 
               description = "Retrieve published articles in a specific category")
    @GetMapping("/category/{category}")
    ResponseEntity<ApiResponse.ArticleDataResponse<ArticleDto>> getArticlesByCategory(
            @Parameter(description = "Category name") @PathVariable String category,
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer size);

    @Operation(summary = "Search articles", 
               description = "Search published articles by title, content, or excerpt")
    @GetMapping("/search")
    ResponseEntity<ApiResponse.ArticleDataResponse<ArticleDto>> searchArticles(
            @Parameter(description = "Search term", required = true) 
            @RequestParam String q,
            @Parameter(description = "Page number (1-based)") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") Integer size);

    @Operation(summary = "Get most viewed articles", 
               description = "Retrieve articles ordered by view count")
    @GetMapping("/most-viewed")
    ResponseEntity<Page<ArticleDto>> getMostViewedArticles(Pageable pageable);

    @Operation(summary = "Get recent articles", 
               description = "Retrieve recently published articles")
    @GetMapping("/recent")
    ResponseEntity<List<ArticleDto>> getRecentArticles(
            @Parameter(description = "Number of days to look back")
            @RequestParam(defaultValue = "7") int days,
            @Parameter(description = "Maximum number of articles")
            @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "Get related articles", 
               description = "Get articles related to a specific article by category and tags")
    @GetMapping("/{id}/related")
    ResponseEntity<List<ArticleDto>> getRelatedArticles(
            @Parameter(description = "Article ID") @PathVariable Long id,
            @Parameter(description = "Maximum number of related articles")
            @RequestParam(defaultValue = "5") int limit);

    @Operation(summary = "Get articles by author", 
               description = "Retrieve articles by a specific author")
    ResponseEntity<Page<ArticleDto>> getArticlesByAuthor(
            @Parameter(description = "Author ID") @PathVariable Long authorId,
            Pageable pageable);

    // ==================== AUTHENTICATED ENDPOINTS ====================

    @Operation(summary = "Create new article", 
               description = "Create a new article (requires contributor or admin role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    ResponseEntity<ArticleDto> createArticle(@Valid @RequestBody ArticleDto articleDto);

    @Operation(summary = "Update article", 
               description = "Update an existing article (requires ownership or admin role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    ResponseEntity<ArticleDto> updateArticle(
            @Parameter(description = "Article ID") Long id,
            @Valid ArticleDto articleDto);

    @Operation(summary = "Delete article", 
               description = "Delete an article (requires ownership or admin role)",
               security = @SecurityRequirement(name = "bearerAuth"))
    ResponseEntity<Void> deleteArticle(
            @Parameter(description = "Article ID") Long id);

    @Operation(summary = "Get current user's articles", 
               description = "Retrieve articles created by the authenticated user",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-articles")
    ResponseEntity<Page<ArticleDto>> getCurrentUserArticles(Pageable pageable);

    // ==================== ADMIN ENDPOINTS ====================

    @Operation(summary = "Get article statistics", 
               description = "Get statistics about articles (admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/statistics")
    ResponseEntity<ArticleService.ArticleStatsDto> getArticleStatistics();

    /**
     * Publish a draft article
     * 
     * @param id Article ID
     * @return Published article
     */
    @Operation(summary = "Publish draft article", 
               description = "Publish a draft article by setting it as published and setting the publish date. Only the author or admin can publish articles.")
    @PatchMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CONTRIBUTOR')")
    ResponseEntity<ApiResponse.SingleArticleDataResponse<ArticleDto>> publishArticle(
            @Parameter(description = "Article ID") @PathVariable Long id);
} 