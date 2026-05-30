package com.skillswap.exception;

/** Сетевые/HTTP сбои при обращении к внешнему API (валютному и любому другому). */
public class ExternalApiException extends RuntimeException {
    public ExternalApiException(String message) {
        super(message);
    }

    public ExternalApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
