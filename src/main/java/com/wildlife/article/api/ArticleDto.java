package com.wildlife.article.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for Article entity.
 * Used for API responses and reducing entity exposure.
 */
public class ArticleDto {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255, message = "Title should be between 5 and 255 characters")
    private String title;

    private String content;

    @NotBlank(message = "Excerpt is required")
    @Size(min = 10, max = 500, message = "Excerpt should be between 10 and 500 characters")
    private String excerpt;

    private Boolean published = false;
    private Boolean featured = false;
    private String category;
    private Integer views = 0;
    private List<String> tags;
    
    // Handle both List (from frontend) and String (for entity) formats
    @JsonProperty("images")
    private List<Map<String, Object>> images;
    
    @JsonProperty("videos") 
    private List<Map<String, Object>> videos;
    
    private LocalDateTime publishDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Author information
    private com.wildlife.user.api.UserDto author;
    private Long authorId;

    // Constructors
    public ArticleDto() {}

    public ArticleDto(String title, String excerpt, Boolean published) {
        this.title = title;
        this.excerpt = excerpt;
        this.published = published;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Map<String, Object>> getImages() {
        return images;
    }

    public void setImages(List<Map<String, Object>> images) {
        this.images = images;
    }

    public List<Map<String, Object>> getVideos() {
        return videos;
    }

    public void setVideos(List<Map<String, Object>> videos) {
        this.videos = videos;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public com.wildlife.user.api.UserDto getAuthor() {
        return author;
    }

    public void setAuthor(com.wildlife.user.api.UserDto author) {
        this.author = author;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    @Override
    public String toString() {
        return "ArticleDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", featured=" + featured +
                ", category='" + category + '\'' +
                ", views=" + views +
                ", authorId=" + authorId +
                '}';
    }
} 