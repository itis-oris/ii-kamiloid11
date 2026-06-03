package com.skillswap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI skillSwapOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SkillSwap API")
                        .description("REST API платформы обмена навыками SkillSwap")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Команда SkillSwap")
                                .email("admin@skillswap.com"))
                );
    }
}
