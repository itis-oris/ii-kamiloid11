package com.skillswap.dto;

import java.time.LocalDateTime;

/** DTO отзыва для отображения в профиле и в детальной странице обмена. */
public record ReviewDto(
        Long id,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        String authorUsername,
        String targetUsername,
        Long exchangeId
) {}
