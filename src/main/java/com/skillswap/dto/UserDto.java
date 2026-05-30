package com.skillswap.dto;

import java.time.LocalDateTime;
import java.util.Set;

/** DTO профиля пользователя для шаблонов. Содержит средний рейтинг и набор ролей. */
public record UserDto(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String bio,
        String avatarUrl,
        LocalDateTime createdAt,
        Boolean isActive,
        Set<String> roles,
        Double averageRating
) {}
