package com.skillswap.exception;

/** Бросается, когда при регистрации логин или e-mail уже заняты. */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
