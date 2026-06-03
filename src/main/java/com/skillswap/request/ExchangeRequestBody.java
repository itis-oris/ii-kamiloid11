package com.skillswap.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRequestBody {

    @NotNull(message = "Не указан ID объявления")
    private Long offerId;

    @Size(max = 1000, message = "Сообщение не должно превышать 1000 символов")
    private String message;
}
