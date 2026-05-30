package com.skillswap.exception;

/** Бросается, когда заявка на обмен по указанному id не найдена. */
public class ExchangeRequestNotFoundException extends RuntimeException {
    public ExchangeRequestNotFoundException(String message) {
        super(message);
    }
}
