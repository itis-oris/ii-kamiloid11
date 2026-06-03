package com.skillswap.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Тело запроса на создание/обновление объявления о навыке")
public class OfferRequest {

    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 3, max = 200)
    @Schema(description = "Заголовок объявления", example = "Python для начинающих")
    private String title;

    @Size(max = 2000)
    @Schema(description = "Подробное описание")
    private String description;

    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("8.0")
    @Schema(description = "Часов на занятие", example = "1.5")
    private Double hoursPerSession;

    @DecimalMin("0.0")
    @Schema(description = "Почасовая ставка (необязательно)", example = "25.0")
    private Double hourlyRate;

    @Size(max = 3)
    @Schema(description = "Код валюты", example = "EUR")
    private String rateCurrency = "EUR";

    @NotNull
    @Min(1)
    @Max(20)
    @Schema(description = "Максимальное число учеников", example = "3")
    private Integer maxStudents;

    @NotNull
    @Schema(description = "Идентификатор навыка из справочника", example = "1")
    private Long skillId;
}
