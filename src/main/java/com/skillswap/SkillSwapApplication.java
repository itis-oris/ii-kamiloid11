package com.skillswap;

import com.skillswap.config.PortCleanupListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SkillSwapApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SkillSwapApplication.class);
        app.addListeners(new PortCleanupListener());
        app.run(args);
    }
}
