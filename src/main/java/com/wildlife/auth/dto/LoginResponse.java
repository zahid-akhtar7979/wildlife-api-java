package com.wildlife.auth.dto;

import com.wildlife.user.api.UserDto;

/**
 * Login response DTO matching frontend expectations
 */
public class LoginResponse {

    private boolean success;
    private String message;
    private String token;
    private UserDto user;

    // Constructors
    public LoginResponse() {}

    public LoginResponse(boolean success, String message, String token, UserDto user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.user = user;
    }

    public static LoginResponse success(String token, UserDto user) {
        return new LoginResponse(true, "Login successful", token, user);
    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message, null, null);
    }

    // Getters and Setters
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null") + '\'' +
                ", user=" + user +
                '}';
    }
} 