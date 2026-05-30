package com.skillswap.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillswap.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp-клиент к публичному API курсов валют (open.er-api.com).
 * Никаких SDK — простой GET + ручной разбор JSON через Jackson.
 * Кэширование верхнего уровня — задача {@link com.skillswap.service.CurrencyService}.
 */
@Component
@Slf4j
public class CurrencyApiClient {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public CurrencyApiClient(
            @Value("${currency.api.base-url}") String baseUrl,
            @Value("${currency.api.timeout-seconds}") int timeout) {
        this.baseUrl = baseUrl;
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Запрашивает курсы относительно базовой валюты.
     * Возвращает плоскую мапу "тикер → курс" в виде BigDecimal,
     * чтобы избежать округлений float/double при последующей конвертации.
     *
     * @throws ExternalApiException если сервис недоступен или вернул ошибку
     */
    public Map<String, BigDecimal> getRates(String baseCurrency) {
        String adresEndpointa = baseUrl + "/" + baseCurrency;
        Request httpZapros = new Request.Builder()
                .url(adresEndpointa)
                .get()
                .build();

        try (Response otvet = httpClient.newCall(httpZapros).execute()) {
            if (!otvet.isSuccessful()) {
                log.error("Внешний API курсов вернул статус {}", otvet.code());
                throw new ExternalApiException("Currency API error: HTTP " + otvet.code());
            }

            String teloOtveta = otvet.body().string();
            JsonNode korenJson = objectMapper.readTree(teloOtveta);

            if (!"success".equals(korenJson.path("result").asText())) {
                log.error("Внешний API курсов вернул result != success");
                throw new ExternalApiException("Currency API returned error result");
            }

            JsonNode uzelKursov = korenJson.path("rates");
            Map<String, BigDecimal> kursy = new HashMap<>();
            // итератор по полям JSON — самый дешёвый способ, без промежуточной мапы
            Iterator<Map.Entry<String, JsonNode>> polya = uzelKursov.fields();
            while (polya.hasNext()) {
                Map.Entry<String, JsonNode> para = polya.next();
                kursy.put(para.getKey(), new BigDecimal(para.getValue().asText()));
            }
            return kursy;
        } catch (ExternalApiException e) {
            // пробрасываем как есть — наш кастомный exception
            throw e;
        } catch (Exception e) {
            log.error("Не удалось получить курсы валют для {}: {}", baseCurrency, e.getMessage(), e);
            throw new ExternalApiException("Failed to fetch currency rates", e);
        }
    }
}
