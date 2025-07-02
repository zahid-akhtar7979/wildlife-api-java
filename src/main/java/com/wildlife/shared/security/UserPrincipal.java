package com.wildlife.shared.security;

import com.wildlife.user.core.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserPrincipal for Spring Security authentication.
 * Wraps User entity for security context.
 */
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String name;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;

    public UserPrincipal(Long id, String email, String name, String password, 
                        Collection<? extends GrantedAuthority> authorities, boolean enabled) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.authorities = authorities;
        this.enabled = enabled;
    }

    /**
     * Create UserPrincipal from User entity
     */
    public static UserPrincipal create(User user) {
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        
        return new UserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getPassword() != null ? user.getPassword() : "",
            Collections.singletonList(authority),
            user.getEnabled()
        );
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    // UserDetails implementation
    @Override
    public String getUsername() {
        return email; // Use email as username
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
} 