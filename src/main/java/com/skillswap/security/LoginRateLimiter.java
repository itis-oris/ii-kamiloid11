package com.skillswap.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Простейший in-memory rate limiter для попыток входа. Хранит счётчик неудач
 * и временное «бан»-окно на пользователя. Для прода стоит переехать в Redis,
 * но для семестрового проекта ConcurrentHashMap-а более чем достаточно.
 *
 * не трогать, сломается — на этом завязан CustomAuthenticationFailureHandler.
 */
@Component
public class LoginRateLimiter {

    private static final int MAKS_POPYTOK = 5;
    // 5 минут в миллисекундах
    private static final long DLITELNOST_BLOKIROVKI_MS = 300_000;

    private final Map<String, AtomicInteger> popytkiVhoda = new ConcurrentHashMap<>();
    private final Map<String, Long> zablokirovanDo = new ConcurrentHashMap<>();

    /**
     * Возвращает true, если пользователь заблокирован прямо сейчас.
     * Заодно лениво чистит устаревшие записи — без отдельного scheduled-таска.
     */
    public boolean isBlocked(String username) {
        Long doKogda = zablokirovanDo.get(username);
        if (doKogda != null && System.currentTimeMillis() < doKogda) {
            return true;
        }
        if (doKogda != null && System.currentTimeMillis() >= doKogda) {
            zablokirovanDo.remove(username);
            popytkiVhoda.remove(username);
        }
        return false;
    }

    /** Регистрирует неудачную попытку. После {@link #MAKS_POPYTOK} включает блок. */
    public void recordFailedAttempt(String username) {
        AtomicInteger schetchik = popytkiVhoda.computeIfAbsent(username, k -> new AtomicInteger(0));
        if (schetchik.incrementAndGet() >= MAKS_POPYTOK) {
            zablokirovanDo.put(username, System.currentTimeMillis() + DLITELNOST_BLOKIROVKI_MS);
        }
    }

    /** Сбрасывает счётчики после успешного входа. */
    public void resetAttempts(String username) {
        popytkiVhoda.remove(username);
        zablokirovanDo.remove(username);
    }
}
