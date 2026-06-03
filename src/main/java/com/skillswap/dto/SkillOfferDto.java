package com.skillswap.dto;

import java.time.LocalDateTime;

public record SkillOfferDto(
        Long id,
        String title,
        String description,
        Double hoursPerSession,
        Double hourlyRate,
        String rateCurrency,
        Integer maxStudents,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String ownerUsername,
        Long ownerId,
        Double ownerRating,
        String skillTitle,
        String skillCategory,
        String skillLevel
) {}
