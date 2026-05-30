package com.skillswap.dto;

import java.time.LocalDateTime;

/**
 * Плоский DTO объявления для рендера на страницах списка/детали.
 * Включает агрегированный рейтинг владельца, чтобы не дёргать БД на каждой карточке.
 */
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
