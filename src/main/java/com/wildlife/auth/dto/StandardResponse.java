package com.wildlife.auth.dto;

/**
 * Standard API response format matching frontend expectations
 */
public class StandardResponse<T> {

    private boolean success;
    private String message;
    private T data;

    // Constructors
    public StandardResponse() {}

    public StandardResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> StandardResponse<T> success(String message, T data) {
        return new StandardResponse<>(true, message, data);
    }

    public static <T> StandardResponse<T> success(T data) {
        return new StandardResponse<>(true, "Operation successful", data);
    }

    public static <T> StandardResponse<T> failure(String message) {
        return new StandardResponse<>(false, message, null);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "StandardResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
} 