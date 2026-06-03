package com.skillswap.dto;

import java.time.LocalDateTime;
import java.util.Set;

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
