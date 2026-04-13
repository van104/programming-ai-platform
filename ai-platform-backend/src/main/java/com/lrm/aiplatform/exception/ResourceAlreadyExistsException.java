package com.lrm.aiplatform.exception;

/**
 * 自定义异常，用于表示资源（如数据库记录）已存在的情况。
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}