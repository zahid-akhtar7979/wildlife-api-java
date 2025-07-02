package com.wildlife.shared.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JPA converter for storing JSON lists in database columns.
 * Converts List<Map<String, Object>> to/from JSON strings.
 */
@Converter(autoApply = false)
public class JsonListConverter implements AttributeConverter<List<Map<String, Object>>, String> {

    private static final Logger logger = LoggerFactory.getLogger(JsonListConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final TypeReference<List<Map<String, Object>>> TYPE_REFERENCE = 
            new TypeReference<List<Map<String, Object>>>() {};

    @Override
    public String convertToDatabaseColumn(List<Map<String, Object>> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "[]";
        }
        
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            logger.error("Error converting list to JSON string", e);
            return "[]";
        }
    }

    @Override
    public List<Map<String, Object>> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            List<Map<String, Object>> result = objectMapper.readValue(dbData, TYPE_REFERENCE);
            return result != null ? result : new ArrayList<>();
        } catch (JsonProcessingException e) {
            logger.error("Error converting JSON string to list: {}", dbData, e);
            return new ArrayList<>();
        }
    }
} 