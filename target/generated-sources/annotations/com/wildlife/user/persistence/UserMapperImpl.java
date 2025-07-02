package com.wildlife.user.persistence;

import com.wildlife.user.api.UserDto;
import com.wildlife.user.core.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-07-02T18:27:19+0530",
    comments = "version: 1.6.2, compiler: javac, environment: Java 17.0.10 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setId( user.getId() );
        userDto.setEmail( user.getEmail() );
        userDto.setName( user.getName() );
        userDto.setRole( user.getRole() );
        userDto.setApproved( user.getApproved() );
        userDto.setEnabled( user.getEnabled() );
        userDto.setCreatedAt( user.getCreatedAt() );
        userDto.setUpdatedAt( user.getUpdatedAt() );

        userDto.setArticleCount( user.getArticles() != null ? (long) user.getArticles().size() : 0L );

        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User user = new User();

        user.setId( userDto.getId() );
        user.setEmail( userDto.getEmail() );
        user.setName( userDto.getName() );
        user.setRole( userDto.getRole() );
        user.setApproved( userDto.getApproved() );
        user.setEnabled( userDto.getEnabled() );

        return user;
    }

    @Override
    public List<UserDto> toDto(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserDto> list = new ArrayList<UserDto>( users.size() );
        for ( User user : users ) {
            list.add( toDto( user ) );
        }

        return list;
    }

    @Override
    public List<User> toEntity(List<UserDto> userDtos) {
        if ( userDtos == null ) {
            return null;
        }

        List<User> list = new ArrayList<User>( userDtos.size() );
        for ( UserDto userDto : userDtos ) {
            list.add( toEntity( userDto ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDto(UserDto userDto, User user) {
        if ( userDto == null ) {
            return;
        }

        if ( userDto.getEmail() != null ) {
            user.setEmail( userDto.getEmail() );
        }
        if ( userDto.getName() != null ) {
            user.setName( userDto.getName() );
        }
        if ( userDto.getRole() != null ) {
            user.setRole( userDto.getRole() );
        }
        if ( userDto.getApproved() != null ) {
            user.setApproved( userDto.getApproved() );
        }
        if ( userDto.getEnabled() != null ) {
            user.setEnabled( userDto.getEnabled() );
        }
    }
}
