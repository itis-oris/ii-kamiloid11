package com.skillswap.api;

import com.skillswap.request.ExchangeRequestBody;
import com.skillswap.service.ExchangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeRestController {

    private final ExchangeService exchangeService;

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
