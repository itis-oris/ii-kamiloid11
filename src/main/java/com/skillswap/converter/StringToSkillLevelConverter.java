package com.skillswap.converter;

import com.skillswap.entity.SkillLevel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Spring-конвертер строки (из query/PathVariable/формы) в {@link SkillLevel}.
 * Безопасно срабатывает на пустую строку — возвращает null вместо исключения,
 * чтобы пользователь мог выбрать «Любой уровень» в фильтре.
 */
@Component
public class StringToSkillLevelConverter implements Converter<String, SkillLevel> {

    @Override
    public SkillLevel convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return SkillLevel.valueOf(source.trim().toUpperCase());
    }
}
