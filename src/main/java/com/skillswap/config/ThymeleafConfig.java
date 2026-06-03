package com.skillswap.config;

import com.skillswap.dialect.SkillSwapDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {

    @Bean
    public SkillSwapDialect skillSwapDialect() {
        return new SkillSwapDialect();
    }
}
