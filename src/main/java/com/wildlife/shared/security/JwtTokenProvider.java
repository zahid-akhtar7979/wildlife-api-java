package com.wildlife.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Token Provider for generating, validating, and parsing JWT tokens.
 * Provides secure token management for the wildlife conservation platform.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${wildlife.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${wildlife.jwt.expiration:86400000}") // 24 hours in milliseconds
    private long jwtExpirationMs;

    @Value("${wildlife.jwt.issuer:wildlife-api}")
    private String jwtIssuer;

    /**
     * Generate JWT token for authenticated user
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationMs);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userPrincipal.getId().toString())
                .issuer(jwtIssuer)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .claim("email", userPrincipal.getEmail())
                .claim("name", userPrincipal.getName())
                .claim("roles", roles)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate token for user ID (for system operations)
     */
    public String generateTokenForUserId(Long userId, String email, String name, List<String> roles) {
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .issuer(jwtIssuer)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .claim("email", email)
                .claim("name", name)
                .claim("roles", roles)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get user ID from JWT token
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    /**
     * Get email from JWT token
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("email", String.class);
    }

    /**
     * Get name from JWT token
     */
    public String getNameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("name", String.class);
    }

    /**
     * Get roles from JWT token
     */
    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("roles", List.class);
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Get expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration();
    }

    /**
     * Check if token is expired
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Get signing key for JWT
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
} 