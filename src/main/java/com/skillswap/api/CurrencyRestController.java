package com.skillswap.api;

import com.skillswap.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyRestController {

    private final CurrencyService currencyService;

    @GetMapping("/convert")
    public ResponseEntity<Map<String, Object>> convert(
            @RequestParam BigDecimal amount,
            @RequestParam String from,
            @RequestParam String to) {
        BigDecimal poschitannoeZnachenie = currencyService.convert(amount, from, to);
        return ResponseEntity.ok(Map.of(
                "amount", amount,
                "from", from,
                "to", to,
                "result", poschitannoeZnachenie
        ));
    }

    @GetMapping("/rates")
    public ResponseEntity<Map<String, BigDecimal>> rates(@RequestParam(defaultValue = "EUR") String base) {
        return ResponseEntity.ok(currencyService.getRates(base));
    }
}
