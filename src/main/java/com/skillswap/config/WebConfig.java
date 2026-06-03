package com.skillswap.config;

import com.skillswap.converter.StringToSkillCategoryConverter;
import com.skillswap.converter.StringToSkillConverter;
import com.skillswap.converter.StringToSkillLevelConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final StringToSkillLevelConverter skillLevelConverter;
    private final StringToSkillConverter skillConverter;
    private final StringToSkillCategoryConverter skillCategoryConverter;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(skillLevelConverter);
        registry.addConverter(skillConverter);
        registry.addConverter(skillCategoryConverter);
    }
}
