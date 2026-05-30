package com.skillswap.exception;

/** Бросается, когда действие пытается выполнить не тот пользователь (не автор, не участник и т.п.). */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
