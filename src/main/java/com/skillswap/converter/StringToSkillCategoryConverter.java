package com.skillswap.converter;

import com.skillswap.entity.SkillCategory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToSkillCategoryConverter implements Converter<String, SkillCategory> {

    @Override
    public SkillCategory convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return SkillCategory.valueOf(source.trim().toUpperCase());
    }
}
