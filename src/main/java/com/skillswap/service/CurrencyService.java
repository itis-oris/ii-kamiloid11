package com.skillswap.service;

import com.skillswap.external.CurrencyApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyApiClient currencyApiClient;

    private static final int ZNAKOV_POSLE_ZAPYATOY = 2;

    @Cacheable(value = "currencyRates", key = "#baseCurrency")
    public Map<String, BigDecimal> getRates(String baseCurrency) {
        log.debug("Запрос курсов валют для базы {}", baseCurrency);
        return currencyApiClient.getRates(baseCurrency);
    }

    public BigDecimal convert(BigDecimal amount, String from, String to) {
        if (from.equalsIgnoreCase(to)) {
            return amount;
        }
        Map<String, BigDecimal> tablitsaKursov = getRates(from);
        BigDecimal koeffitsient = tablitsaKursov.get(to.toUpperCase());
        if (koeffitsient == null) {
            log.warn("Валюта {} не найдена в курсах относительно {}", to, from);
            return amount;
        }
        return amount.multiply(koeffitsient).setScale(ZNAKOV_POSLE_ZAPYATOY, RoundingMode.HALF_UP);
    }
}
