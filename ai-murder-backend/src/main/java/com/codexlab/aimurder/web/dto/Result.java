package com.codexlab.aimurder.web.dto;

public record Result<T>(
        boolean success,
        String code,
        String message,
        T data
) {

    public static <T> Result<T> success(T data) {
        return new Result<>(true, "SUCCESS", "ok", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(true, "SUCCESS", message, data);
    }

    public static <T> Result<T> failure(String code, String message) {
        return new Result<>(false, code, message, null);
    }
}
