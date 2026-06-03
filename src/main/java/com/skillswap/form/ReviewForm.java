package com.skillswap.form;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {

    @NotNull(message = "Поставьте оценку")
    @Min(value = 1, message = "Минимальная оценка — 1")
    @Max(value = 5, message = "Максимальная оценка — 5")
    private Integer rating;

    @Size(max = 1000, message = "Комментарий не должен превышать 1000 символов")
    private String comment;
}
