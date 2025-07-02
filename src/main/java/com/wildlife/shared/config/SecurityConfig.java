package com.wildlife.shared.config;

import com.wildlife.shared.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration with JWT authentication.
 * Provides role-based access control and CORS configuration.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${wildlife.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${wildlife.cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${wildlife.cors.allowed-headers}")
    private List<String> allowedHeaders;

    @Value("${wildlife.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${wildlife.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for REST API
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure session management (stateless for JWT)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure authorization rules - TEMPORARILY PERMISSIVE FOR TESTING
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/", "/favicon.ico", "/robots.txt").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api-docs/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/debug/**").permitAll()
                
                // TEMPORARY: Allow all API endpoints for testing
                .requestMatchers("/api/**").permitAll()
                
                // All other requests
                .anyRequest().authenticated())
            
            // Add JWT authentication filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Set allowed origins
        if (allowedOrigins != null && !allowedOrigins.isEmpty()) {
            configuration.setAllowedOrigins(allowedOrigins);
        } else {
            configuration.setAllowedOriginPatterns(List.of("*"));
        }
        
        // Set allowed methods
        if (allowedMethods != null && !allowedMethods.isEmpty()) {
            configuration.setAllowedMethods(allowedMethods);
        } else {
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        }
        
        // Set allowed headers
        if (allowedHeaders != null && !allowedHeaders.isEmpty()) {
            configuration.setAllowedHeaders(allowedHeaders);
        } else {
            configuration.setAllowedHeaders(List.of("*"));
        }
        
        // Set other CORS properties
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        // Expose commonly needed headers
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "X-Total-Count",
            "X-Total-Pages", 
            "Link"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 