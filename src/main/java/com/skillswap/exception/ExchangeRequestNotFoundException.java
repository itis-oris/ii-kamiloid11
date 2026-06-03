package com.skillswap.exception;

public class ExchangeRequestNotFoundException extends RuntimeException {
    public ExchangeRequestNotFoundException(String message) {
        super(message);
    }
}
