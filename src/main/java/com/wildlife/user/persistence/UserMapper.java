package com.wildlife.user.persistence;

import com.wildlife.user.api.UserDto;
import com.wildlife.user.core.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for User entity and UserDto conversion.
 * Provides automatic mapping between entity and DTO objects.
 */
@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Convert User entity to UserDto
     */
    @Mapping(target = "articleCount", expression = "java(user.getArticles() != null ? (long) user.getArticles().size() : 0L)")
    UserDto toDto(User user);

    /**
     * Convert UserDto to User entity
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserDto userDto);

    /**
     * Convert list of User entities to list of UserDtos
     */
    List<UserDto> toDto(List<User> users);

    /**
     * Convert list of UserDtos to list of User entities
     */
    List<User> toEntity(List<UserDto> userDtos);

    /**
     * Update existing User entity with UserDto data
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(UserDto userDto, @MappingTarget User user);
} 