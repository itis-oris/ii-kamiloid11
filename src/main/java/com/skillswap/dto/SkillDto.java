package com.skillswap.dto;

/** DTO навыка для дропдаунов в формах объявлений. */
public record SkillDto(
        Long id,
        String title,
        String description,
        String category,
        String level
) {}
