package com.skillswap.api;

import com.skillswap.request.ExchangeRequestBody;
import com.skillswap.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * REST-эндпоинт отправки заявки на обмен из карточки объявления.
 * Возвращает JSON, чтобы фронт мог показать успех/ошибку без перезагрузки.
 */
@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeRestController {

    private final ExchangeService exchangeService;

    /** Создать заявку на обмен по указанному в теле объявлению. */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> createRequest(@Valid @RequestBody ExchangeRequestBody body,
                                                              Principal principal) {
        exchangeService.createRequest(body.getOfferId(), body.getMessage(), principal.getName());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Заявка на обмен успешно отправлена"
        ));
    }
}
