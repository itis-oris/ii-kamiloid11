package com.skillswap.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimiter {

    private static final int MAKS_POPYTOK = 5;
    private static final long DLITELNOST_BLOKIROVKI_MS = 300_000;

    private final Map<String, AtomicInteger> popytkiVhoda = new ConcurrentHashMap<>();
    private final Map<String, Long> zablokirovanDo = new ConcurrentHashMap<>();

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

    public void recordFailedAttempt(String username) {
        AtomicInteger schetchik = popytkiVhoda.computeIfAbsent(username, k -> new AtomicInteger(0));
        if (schetchik.incrementAndGet() >= MAKS_POPYTOK) {
            zablokirovanDo.put(username, System.currentTimeMillis() + DLITELNOST_BLOKIROVKI_MS);
        }
    }

    public void resetAttempts(String username) {
        popytkiVhoda.remove(username);
        zablokirovanDo.remove(username);
    }
}
