package com.skillswap.controller;

import com.skillswap.exception.UserAlreadyExistsException;
import com.skillswap.form.RegistrationForm;
import com.skillswap.security.LoginRateLimiter;
import com.skillswap.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер аутентификации: страница логина (форма обрабатывается
 * самим Spring Security) и страница регистрации с собственной обработкой POST.
 */
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    // rateLimiter намеренно остаётся под рукой — на случай если решим
    // подцепить его и сюда (сейчас он висит в фильтре логина)
    private final LoginRateLimiter rateLimiter;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /** GET формы регистрации. */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "auth/register";
    }

    /**
     * POST формы регистрации: валидирует, проверяет совпадение паролей,
     * вызывает {@link UserService#register} и редиректит на логин.
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "match", "Пароли не совпадают");
        }
        if (result.hasErrors()) {
            return "auth/register";
        }
        try {
            userService.register(form);
            redirectAttributes.addFlashAttribute("success", "Регистрация прошла успешно! Теперь войдите в систему.");
            return "redirect:/auth/login";
        } catch (UserAlreadyExistsException oshibkaDublikata) {
            // не оборачиваем в ValidationException — UI и так покажет красную плашку
            result.rejectValue("username", "exists", oshibkaDublikata.getMessage());
            return "auth/register";
        }
    }
}
