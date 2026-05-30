package com.skillswap.dialect;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

/**
 * Кастомный Thymeleaf-диалект для платформы. Регистрирует префикс {@code ss:}
 * и подвешивает к нему процессор атрибута rating-stars — он рендерит звёзды
 * рейтинга прямо в шаблонах.
 */
public class SkillSwapDialect extends AbstractProcessorDialect {

    private static final String NAME = "SkillSwap Dialect";
    private static final String PREFIX = "ss";

    public SkillSwapDialect() {
        super(NAME, PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
    }

    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        Set<IProcessor> processors = new HashSet<>();
        processors.add(new RatingStarsProcessor(dialectPrefix));
        return processors;
    }
}
