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
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public interface ArticleMapper {
    
    Logger logger = LoggerFactory.getLogger(ArticleMapper.class);
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert Article entity to ArticleDto
     */
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "author", ignore = true)
    ArticleDto toDto(Article article);

    /**
     * Convert ArticleDto to Article entity
     */
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    Article toEntity(ArticleDto articleDto);

    /**
     * Convert list of Article entities to list of ArticleDtos
     */
    List<ArticleDto> toDto(List<Article> articles);

    /**
     * Convert list of ArticleDtos to list of Article entities
     */
    List<Article> toEntity(List<ArticleDto> articleDtos);

    /**
     * Update existing Article entity with ArticleDto data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publishDate", ignore = true)
    @Mapping(target = "views", ignore = true)
    void updateEntityFromDto(ArticleDto articleDto, @MappingTarget Article article);

    /**
     * Convert JSON string to List of image objects
     */
    default List<Map<String, Object>> map(String json) {
        if (json == null || json.trim().isEmpty() || "[]".equals(json.trim())) {
            return new ArrayList<>();
        }
        
        try {
            TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
            return objectMapper.readValue(json, typeRef);
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON string to list: {}", json, e);
            return new ArrayList<>();
        }
    }

    /**
     * Convert List of objects to JSON string
     */
    default String map(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            logger.error("Error converting list to JSON string", e);
            return "[]";
        }
    }
} 