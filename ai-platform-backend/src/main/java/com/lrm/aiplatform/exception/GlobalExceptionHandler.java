package com.lrm.aiplatform.exception;

import com.lrm.aiplatform.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获Controller层抛出的异常，并返回统一的错误响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理资源已存在的异常
     * 当服务层抛出 ResourceAlreadyExistsException 时，此方法被调用。
     * @param e 捕获到的异常
     * @return 返回给客户端的统一错误结果
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 返回 409 Conflict 状态码
    public Result<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        return new Result<>(HttpStatus.CONFLICT.value(), e.getMessage(), null);
    }

    /**
     * 处理无效参数异常
     * 当服务层抛出 IllegalArgumentException 时（例如，用户不存在），此方法被调用。
     * @param e 捕获到的异常
     * @return 返回给客户端的统一错误结果
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 返回 400 Bad Request
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return new Result<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null);
    }
}