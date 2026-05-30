package com.skillswap.exception;

/** Бросается, когда запрошенное объявление не существует или скрыто. */
public class SkillOfferNotFoundException extends RuntimeException {
    public SkillOfferNotFoundException(String message) {
        super(message);
    }
}
