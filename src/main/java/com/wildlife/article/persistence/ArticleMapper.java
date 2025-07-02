package com.wildlife.article.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wildlife.article.api.ArticleDto;
import com.wildlife.article.core.Article;
import com.wildlife.user.persistence.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * MapStruct mapper for Article entity and ArticleDto conversion.
 * Provides automatic mapping between entity and DTO objects.
 */
@Mapper(componentModel = "spring", 
        uses = UserMapper.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class ArticleMapper {
    
    protected static final Logger logger = LoggerFactory.getLogger(ArticleMapper.class);
    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    protected UserMapper userMapper;

    /**
     * Convert Article entity to ArticleDto
     */
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "images", source = "images", qualifiedByName = "stringToImageList")
    @Mapping(target = "videos", source = "videos", qualifiedByName = "stringToVideoList")
    public abstract ArticleDto toDto(Article article);

    /**
     * Convert ArticleDto to Article entity
     */
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "images", source = "images", qualifiedByName = "imageListToString")
    @Mapping(target = "videos", source = "videos", qualifiedByName = "videoListToString")
    public abstract Article toEntity(ArticleDto articleDto);

    /**
     * Convert list of Article entities to list of ArticleDtos
     */
    public abstract List<ArticleDto> toDto(List<Article> articles);

    /**
     * Convert list of ArticleDtos to list of Article entities
     */
    public abstract List<Article> toEntity(List<ArticleDto> articleDtos);

    /**
     * Update existing Article entity with ArticleDto data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "images", source = "images", qualifiedByName = "imageListToString")
    @Mapping(target = "videos", source = "videos", qualifiedByName = "videoListToString")
    public abstract void updateEntityFromDto(ArticleDto articleDto, @MappingTarget Article article);

    /**
     * Convert JSON string to List of image objects
     */
    @Named("stringToImageList")
    protected List<Map<String, Object>> stringToImageList(String imagesJson) {
        if (imagesJson == null || imagesJson.trim().isEmpty() || "[]".equals(imagesJson.trim())) {
            return new ArrayList<>();
        }
        
        try {
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
            return objectMapper.readValue(imagesJson, typeRef);
        } catch (JsonProcessingException e) {
            logger.error("Error converting images JSON string to list: {}", imagesJson, e);
            return new ArrayList<>();
        }
    }

    /**
     * Convert JSON string to List of video objects
     */
    @Named("stringToVideoList")
    protected List<Map<String, Object>> stringToVideoList(String videosJson) {
        if (videosJson == null || videosJson.trim().isEmpty() || "[]".equals(videosJson.trim())) {
            return new ArrayList<>();
        }
        
        try {
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
            return objectMapper.readValue(videosJson, typeRef);
        } catch (JsonProcessingException e) {
            logger.error("Error converting videos JSON string to list: {}", videosJson, e);
            return new ArrayList<>();
        }
    }

    /**
     * Convert List of image objects to JSON string
     */
    @Named("imageListToString")
    protected String imageListToString(List<Map<String, Object>> images) {
        if (images == null || images.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(images);
        } catch (JsonProcessingException e) {
            logger.error("Error converting images list to JSON string", e);
            return "[]";
        }
    }

    /**
     * Convert List of video objects to JSON string
     */
    @Named("videoListToString")
    protected String videoListToString(List<Map<String, Object>> videos) {
        if (videos == null || videos.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(videos);
        } catch (JsonProcessingException e) {
            logger.error("Error converting videos list to JSON string", e);
            return "[]";
        }
    }
} 