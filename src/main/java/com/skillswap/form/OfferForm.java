package com.skillswap.form;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferForm {

    @NotBlank(message = "Заголовок обязателен")
    @Size(min = 3, max = 200, message = "Заголовок должен содержать от 3 до 200 символов")
    private String title;

    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;

    @NotNull(message = "Укажите длительность занятия")
    @DecimalMin(value = "0.5", message = "Минимум 0.5 часа")
    @DecimalMax(value = "8.0", message = "Максимум 8 часов")
    private Double hoursPerSession;

    @DecimalMin(value = "0.0", message = "Стоимость не может быть отрицательной")
    private Double hourlyRate;

    @Size(max = 3)
    private String rateCurrency = "EUR";

    @NotNull(message = "Укажите максимальное число учеников")
    @Min(value = 1, message = "Должен быть хотя бы 1 ученик")
    @Max(value = 20, message = "Максимум 20 учеников")
    private Integer maxStudents;

    @NotNull(message = "Выберите навык")
    private Long skillId;
}
