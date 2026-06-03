package com.skillswap.converter;

import com.skillswap.entity.Skill;
import com.skillswap.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StringToSkillConverter implements Converter<String, Skill> {

    private final SkillRepository skillRepository;

    @Override
    public Skill convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        Long id = Long.parseLong(source.trim());
        return skillRepository.findById(id).orElse(null);
    }
}
