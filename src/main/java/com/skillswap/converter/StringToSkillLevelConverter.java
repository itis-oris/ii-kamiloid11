package com.skillswap.converter;

import com.skillswap.entity.SkillLevel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

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
