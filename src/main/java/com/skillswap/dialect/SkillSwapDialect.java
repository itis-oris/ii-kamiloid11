package com.skillswap.dialect;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import java.util.HashSet;
import java.util.Set;

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
