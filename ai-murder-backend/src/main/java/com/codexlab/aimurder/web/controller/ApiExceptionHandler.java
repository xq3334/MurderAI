package com.codexlab.aimurder.web.controller;

import com.codexlab.aimurder.web.dto.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * 将非法参数异常转换为统一的失败响应。
     *
     * @param exception 抛出的异常对象
     * @return 统一结构的失败结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgument(IllegalArgumentException exception) {
        return Result.failure("BAD_REQUEST", exception.getMessage());
    }

    /**
     * 将参数校验异常转换为统一的失败响应。
     *
     * @param exception 抛出的校验异常对象
     * @return 统一结构的失败结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldError() == null
                ? "request is invalid"
                : exception.getBindingResult().getFieldError().getDefaultMessage();
        return Result.failure("VALIDATION_ERROR", message);
    }
}
