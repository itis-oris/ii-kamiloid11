package com.skillswap.dto;

public record SkillDto(
        Long id,
        String title,
        String description,
        String category,
        String level
) {}
