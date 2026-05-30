package com.skillswap.service;

import com.skillswap.external.CurrencyApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * Сервис конвертации валют для отображения цены занятий пользователям из разных стран.
 * Курсы тянутся через {@link CurrencyApiClient}, ответ кладётся в Redis на час,
 * чтобы не упереться в лимиты бесплатного публичного API курсов.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyApiClient currencyApiClient;

    // Количество знаков после запятой в результате конвертации.
    // Двух хватает — на странице оффера всё равно показываем округлённую цифру.
    private static final int ZNAKOV_POSLE_ZAPYATOY = 2;

    /**
     * Достаёт карту курсов "валюта → курс" относительно указанной базовой.
     * Кэш Spring-а удерживает ответ под ключом {@code baseCurrency} (см. application.yml,
     * TTL на бакет {@code currencyRates}).
     */
    @Cacheable(value = "currencyRates", key = "#baseCurrency")
    public Map<String, BigDecimal> getRates(String baseCurrency) {
        log.debug("Запрос курсов валют для базы {}", baseCurrency);
        return currencyApiClient.getRates(baseCurrency);
    }

    /**
     * Переводит сумму из одной валюты в другую. Если валюты совпадают —
     * возвращает входное значение без обращения к внешнему API.
     */
    public BigDecimal convert(BigDecimal amount, String from, String to) {
        // добавил 14.03 после того как всё падало на запросах вида EUR→EUR
        if (from.equalsIgnoreCase(to)) {
            return amount;
        }
        Map<String, BigDecimal> tablitsaKursov = getRates(from);
        BigDecimal koeffitsient = tablitsaKursov.get(to.toUpperCase());
        if (koeffitsient == null) {
            log.warn("Валюта {} не найдена в курсах относительно {}", to, from);
            // не падаем: вернём как есть, фронт покажет хотя бы оригинал
            return amount;
        }
        return amount.multiply(koeffitsient).setScale(ZNAKOV_POSLE_ZAPYATOY, RoundingMode.HALF_UP);
    }
}
