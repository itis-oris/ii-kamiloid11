package com.skillswap.config;

import com.skillswap.entity.User;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * После старта приложения проверяет, что у демо-пользователей лежит актуальный BCrypt-hash
 * под известный пароль. Это нужно потому, что Flyway вставляет фиксированный hash,
 * который мог быть посчитан другой солью — при первом запуске мы его перегенерируем.
 *
 * хз почему но без этого не работает корректный логин админа после миграций.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Map<String, String> DEMO_PASSWORDS = Map.of(
            "admin", "admin123",
            "johndoe", "user123",
            "janedoe", "user123"
    );

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        DEMO_PASSWORDS.forEach((username, rawPassword) ->
                userRepository.findByUsername(username).ifPresent(user -> {
                    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                        user.setPasswordHash(passwordEncoder.encode(rawPassword));
                        userRepository.save(user);
                        log.info("Re-hashed password for demo user: {}", username);
                    }
                })
        );
    }
}
