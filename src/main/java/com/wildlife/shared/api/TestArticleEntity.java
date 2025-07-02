package com.wildlife.shared.api;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Minimal test entity to isolate the Article mapping issue
 */
@Entity
@Table(name = "articles")
public class TestArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "excerpt")
    private String excerpt;

    @Column(name = "published")
    private Boolean published;

    @Column(name = "featured")
    private Boolean featured;

    @Column(name = "category")
    private String category;

    @Column(name = "views")
    private Integer views;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "author_id")
    private Long authorId;

    // Constructors
    public TestArticleEntity() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getExcerpt() { return excerpt; }
    public void setExcerpt(String excerpt) { this.excerpt = excerpt; }

    public Boolean getPublished() { return published; }
    public void setPublished(Boolean published) { this.published = published; }

    public Boolean getFeatured() { return featured; }
    public void setFeatured(Boolean featured) { this.featured = featured; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getViews() { return views; }
    public void setViews(Integer views) { this.views = views; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
} 