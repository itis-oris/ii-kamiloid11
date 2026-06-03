package com.skillswap.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {

    @NotBlank(message = "Укажите логин")
    @Size(min = 3, max = 50, message = "Логин должен быть от 3 до 50 символов")
    private String username;

    @NotBlank(message = "Укажите электронную почту")
    @Email(message = "Некорректный формат e-mail")
    private String email;

    @NotBlank(message = "Введите пароль")
    @Size(min = 6, max = 100, message = "Пароль должен быть не короче 6 символов")
    private String password;

    @NotBlank(message = "Повторите пароль для подтверждения")
    private String confirmPassword;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;
}
