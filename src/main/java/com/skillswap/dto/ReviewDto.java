package com.skillswap.dto;

import java.time.LocalDateTime;

public record ReviewDto(
        Long id,
        Integer rating,
        String comment,
        LocalDateTime createdAt,
        String authorUsername,
        String targetUsername,
        Long exchangeId
) {}
