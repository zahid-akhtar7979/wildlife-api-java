package com.wildlife.shared.dto;

import org.springframework.data.domain.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Standard API Response wrapper for frontend compatibility
 * Transforms Spring Boot pagination format to frontend expected format
 */
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private List<T> data;
    private PaginationInfo pagination;
    
    // Constructors
    public ApiResponse() {
        this.success = true;
    }
    
    public ApiResponse(List<T> data) {
        this.success = true;
        this.data = data;
    }
    
    public ApiResponse(Page<T> page) {
        this.success = true;
        this.data = page.getContent();
        this.pagination = new PaginationInfo(page);
    }
    
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Static factory methods
    public static <T> ApiResponse<T> success(List<T> data) {
        return new ApiResponse<>(data);
    }
    
    public static <T> ApiResponse<T> success(Page<T> page) {
        return new ApiResponse<>(page);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<T> getData() {
        return data;
    }
    
    public void setData(List<T> data) {
        this.data = data;
    }
    
    public PaginationInfo getPagination() {
        return pagination;
    }
    
    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }
    
    /**
     * Pagination information in frontend expected format
     */
    public static class PaginationInfo {
        private int current;
        private int pages;
        private long total;
        private boolean hasNext;
        private boolean hasPrev;
        
        public PaginationInfo() {}
        
        public PaginationInfo(Page<?> page) {
            this.current = page.getNumber() + 1; // Convert 0-based to 1-based
            this.pages = page.getTotalPages();
            this.total = page.getTotalElements();
            this.hasNext = page.hasNext();
            this.hasPrev = page.hasPrevious();
        }
        
        public PaginationInfo(int current, int pages, long total, boolean hasNext, boolean hasPrev) {
            this.current = current;
            this.pages = pages;
            this.total = total;
            this.hasNext = hasNext;
            this.hasPrev = hasPrev;
        }
        
        // Getters and setters
        public int getCurrent() {
            return current;
        }
        
        public void setCurrent(int current) {
            this.current = current;
        }
        
        public int getPages() {
            return pages;
        }
        
        public void setPages(int pages) {
            this.pages = pages;
        }
        
        public long getTotal() {
            return total;
        }
        
        public void setTotal(long total) {
            this.total = total;
        }
        
        @JsonProperty("hasNext")
        public boolean isHasNext() {
            return hasNext;
        }
        
        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }
        
        @JsonProperty("hasPrev")
        public boolean isHasPrev() {
            return hasPrev;
        }
        
        public void setHasPrev(boolean hasPrev) {
            this.hasPrev = hasPrev;
        }
    }

    /**
     * Article-specific response format to match frontend expectations
     * Returns: { "data": { "articles": [...], "pagination": {...} } }
     */
    public static class ArticleDataResponse<T> {
        private ArticleData<T> data;
        
        public ArticleDataResponse(ArticleData<T> data) {
            this.data = data;
        }
        
        public ArticleData<T> getData() {
            return data;
        }
        
        public void setData(ArticleData<T> data) {
            this.data = data;
        }
        
        public static class ArticleData<T> {
            private List<T> articles;
            private PaginationInfo pagination;
            
            public ArticleData(List<T> articles, PaginationInfo pagination) {
                this.articles = articles;
                this.pagination = pagination;
            }
            
            public List<T> getArticles() {
                return articles;
            }
            
            public void setArticles(List<T> articles) {
                this.articles = articles;
            }
            
            public PaginationInfo getPagination() {
                return pagination;
            }
            
            public void setPagination(PaginationInfo pagination) {
                this.pagination = pagination;
            }
        }
        
        // Static factory method for creating from Page
        public static <T> ArticleDataResponse<T> fromPage(Page<T> page) {
            PaginationInfo pagination = new PaginationInfo(
                page.getNumber() + 1, // Convert back to 1-based for frontend
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext(),
                page.hasPrevious()
            );
            ArticleData<T> data = new ArticleData<>(page.getContent(), pagination);
            return new ArticleDataResponse<>(data);
        }
    }

    /**
     * Single article response format for individual article endpoints
     * Returns: { "data": { "article": {...} } }
     */
    public static class SingleArticleDataResponse<T> {
        private SingleArticleData<T> data;
        
        public SingleArticleDataResponse(SingleArticleData<T> data) {
            this.data = data;
        }
        
        public SingleArticleData<T> getData() {
            return data;
        }
        
        public void setData(SingleArticleData<T> data) {
            this.data = data;
        }
        
        public static class SingleArticleData<T> {
            private T article;
            
            public SingleArticleData(T article) {
                this.article = article;
            }
            
            public T getArticle() {
                return article;
            }
            
            public void setArticle(T article) {
                this.article = article;
            }
        }
        
        // Static factory method for creating from article
        public static <T> SingleArticleDataResponse<T> fromArticle(T article) {
            SingleArticleData<T> data = new SingleArticleData<>(article);
            return new SingleArticleDataResponse<>(data);
        }
    }

    /**
     * Standard data response format for other operations
     * Returns: { "success": true, "data": {...}, "message": "..." }
     */
    public static class StandardResponse<T> {
        private boolean success;
        private String message;
        private T data;
        
        public StandardResponse() {
            this.success = true;
        }
        
        public StandardResponse(T data) {
            this.success = true;
            this.data = data;
        }
        
        public StandardResponse(T data, String message) {
            this.success = true;
            this.data = data;
            this.message = message;
        }
        
        public StandardResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        // Static factory methods
        public static <T> StandardResponse<T> success(T data) {
            return new StandardResponse<>(data);
        }
        
        public static <T> StandardResponse<T> success(T data, String message) {
            return new StandardResponse<>(data, message);
        }
        
        public static <T> StandardResponse<T> error(String message) {
            return new StandardResponse<>(false, message);
        }
        
        // Getters and setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public T getData() {
            return data;
        }
        
        public void setData(T data) {
            this.data = data;
        }
    }

    // Categories Data Response for categories endpoint
    public static class CategoriesDataResponse {
        private CategoriesData data;

        public CategoriesDataResponse(CategoriesData data) {
            this.data = data;
        }

        public CategoriesData getData() {
            return data;
        }

        public void setData(CategoriesData data) {
            this.data = data;
        }

        public static class CategoriesData {
            private List<String> categories;

            public CategoriesData(List<String> categories) {
                this.categories = categories;
            }

            public List<String> getCategories() {
                return categories;
            }

            public void setCategories(List<String> categories) {
                this.categories = categories;
            }
        }

        // Static factory method for creating from categories list
        public static CategoriesDataResponse fromCategories(List<String> categories) {
            CategoriesData data = new CategoriesData(categories);
            return new CategoriesDataResponse(data);
        }
    }

    // Tags Data Response for tags endpoint
    public static class TagsDataResponse {
        private TagsData data;

        public TagsDataResponse(TagsData data) {
            this.data = data;
        }

        public TagsData getData() {
            return data;
        }

        public void setData(TagsData data) {
            this.data = data;
        }

        public static class TagsData {
            private List<String> tags;

            public TagsData(List<String> tags) {
                this.tags = tags;
            }

            public List<String> getTags() {
                return tags;
            }

            public void setTags(List<String> tags) {
                this.tags = tags;
            }
        }

        // Static factory method for creating from tags list
        public static TagsDataResponse fromTags(List<String> tags) {
            TagsData data = new TagsData(tags);
            return new TagsDataResponse(data);
        }
    }

    // Auth Success Response for login/register
    public static class AuthSuccessResponse {
        private boolean success;
        private String token;
        private Object user;
        private String message;

        public AuthSuccessResponse(boolean success, String token, Object user, String message) {
            this.success = success;
            this.token = token;
            this.user = user;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public Object getUser() {
            return user;
        }

        public void setUser(Object user) {
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        // Static factory method
        public static AuthSuccessResponse create(String token, Object user, String message) {
            return new AuthSuccessResponse(true, token, user, message);
        }
    }

    // Upload Success Response for file uploads
    public static class UploadSuccessResponse {
        private boolean success;
        private UploadData data;
        private String message;

        public UploadSuccessResponse(boolean success, UploadData data, String message) {
            this.success = success;
            this.data = data;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public UploadData getData() {
            return data;
        }

        public void setData(UploadData data) {
            this.data = data;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public static class UploadData {
            private String url;
            private String publicId;
            private String format;
            private long bytes;
            private String resourceType;

            public UploadData(String url, String publicId, String format, long bytes, String resourceType) {
                this.url = url;
                this.publicId = publicId;
                this.format = format;
                this.bytes = bytes;
                this.resourceType = resourceType;
            }

            // Getters and setters
            public String getUrl() { return url; }
            public void setUrl(String url) { this.url = url; }
            public String getPublicId() { return publicId; }
            public void setPublicId(String publicId) { this.publicId = publicId; }
            public String getFormat() { return format; }
            public void setFormat(String format) { this.format = format; }
            public long getBytes() { return bytes; }
            public void setBytes(long bytes) { this.bytes = bytes; }
            public String getResourceType() { return resourceType; }
            public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        }

        // Static factory method
        public static UploadSuccessResponse create(String url, String publicId, String format, long bytes, String resourceType, String message) {
            UploadData data = new UploadData(url, publicId, format, bytes, resourceType);
            return new UploadSuccessResponse(true, data, message);
        }
    }

    // Generic Success Response for operations like delete
    public static class SuccessResponse {
        private boolean success;
        private String message;

        public SuccessResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        // Static factory method
        public static SuccessResponse create(String message) {
            return new SuccessResponse(true, message);
        }
    }

    // User Data Response for profile/current user endpoints
    public static class UserDataResponse {
        private boolean success;
        private UserData data;

        public UserDataResponse(boolean success, UserData data) {
            this.success = success;
            this.data = data;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public UserData getData() {
            return data;
        }

        public void setData(UserData data) {
            this.data = data;
        }

        public static class UserData {
            private Object user;

            public UserData(Object user) {
                this.user = user;
            }

            public Object getUser() {
                return user;
            }

            public void setUser(Object user) {
                this.user = user;
            }
        }

        // Static factory method
        public static UserDataResponse create(Object user) {
            UserData data = new UserData(user);
            return new UserDataResponse(true, data);
        }
    }
} 