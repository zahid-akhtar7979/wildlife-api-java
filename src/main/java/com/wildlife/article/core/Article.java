package com.wildlife.article.core;

import com.wildlife.shared.config.JsonListConverter;
import com.wildlife.user.core.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Article entity representing wildlife conservation content.
 * Supports rich content with images, videos, and metadata.
 */
@Entity
@Table(name = "articles", indexes = {
    @Index(name = "idx_article_published", columnList = "published"),
    @Index(name = "idx_article_featured", columnList = "featured"),
    @Index(name = "idx_article_category", columnList = "category"),
    @Index(name = "idx_article_author", columnList = "author_id"),
    @Index(name = "idx_article_publish_date", columnList = "publish_date"),
    @Index(name = "idx_article_created_at", columnList = "created_at")
})
@EntityListeners(AuditingEntityListener.class)
public class Article {

    // Fields ordered to match Hibernate's alphabetical SQL column generation:
    // id, author_id, category, content, created_at, excerpt, featured, images, publish_date, published, title, updated_at, videos, views

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "author_id")
    private Long authorId;

    @Size(max = 100, message = "Category should not exceed 100 characters")
    @Column(name = "category", length = 100)
    private String category;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotBlank(message = "Excerpt is required")
    @Size(min = 10, max = 500, message = "Excerpt should be between 10 and 500 characters")
    @Column(name = "excerpt", nullable = false, length = 500)
    private String excerpt;

    @Column(name = "featured", nullable = false)
    private Boolean featured = false;

    @Convert(converter = JsonListConverter.class)
    @Column(name = "images", columnDefinition = "TEXT")
    private List<Map<String, Object>> images = new ArrayList<>();

    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @Column(name = "published", nullable = false)
    private Boolean published = false;

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255, message = "Title should be between 5 and 255 characters")
    @Column(name = "title", nullable = false)
    private String title;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Convert(converter = JsonListConverter.class)
    @Column(name = "videos", columnDefinition = "TEXT")
    private List<Map<String, Object>> videos = new ArrayList<>();

    @Column(name = "views", nullable = false)
    private Integer views = 0;

    // Temporarily comment out @ElementCollection to isolate the issue
    // @ElementCollection(fetch = FetchType.LAZY)
    // @CollectionTable(name = "article_tags", joinColumns = @JoinColumn(name = "article_id"))
    // @Column(name = "tag", length = 50)
    @Transient
    private List<String> tags = new ArrayList<>();

    // Constructors
    public Article() {}

    public Article(String title, String excerpt, Long authorId) {
        this.title = title;
        this.excerpt = excerpt;
        this.authorId = authorId;
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
        if (Boolean.TRUE.equals(published) && publishDate == null) {
            publishDate = LocalDateTime.now();
        }
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
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public List<Map<String, Object>> getImages() {
        return images;
    }

    public void setImages(List<Map<String, Object>> images) {
        this.images = images != null ? images : new ArrayList<>();
    }

    public List<Map<String, Object>> getVideos() {
        return videos;
    }

    public void setVideos(List<Map<String, Object>> videos) {
        this.videos = videos != null ? videos : new ArrayList<>();
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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    // Utility methods
    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !tags.contains(tag.trim())) {
            tags.add(tag.trim());
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public void incrementViews() {
        this.views = (this.views == null ? 0 : this.views) + 1;
    }

    public boolean isPublished() {
        return Boolean.TRUE.equals(published);
    }

    public boolean isFeatured() {
        return Boolean.TRUE.equals(featured);
    }

    public boolean isDraft() {
        return !isPublished();
    }

    public boolean canBeAccessedBy(User user) {
        if (isPublished()) {
            return true; // Published articles are public
        }
        
        if (user == null) {
            return false; // Drafts require authentication
        }
        
        // Only author or admin can access drafts
        return user.isAdmin() || Objects.equals(authorId, user.getId());
    }

    public void publish() {
        this.published = true;
        if (this.publishDate == null) {
            this.publishDate = LocalDateTime.now();
        }
    }

    public void unpublish() {
        this.published = false;
    }

    // equals, hashCode, toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", published=" + published +
                ", featured=" + featured +
                ", category='" + category + '\'' +
                ", views=" + views +
                ", authorId=" + authorId +
                ", createdAt=" + createdAt +
                '}';
    }
} 