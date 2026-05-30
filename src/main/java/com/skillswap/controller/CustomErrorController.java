package com.skillswap.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Подменяет дефолтную whitelabel-страницу Spring Boot на наши шаблоны 404/403/500.
 * Срабатывает для случаев, когда исключение не дошло до GlobalExceptionHandler
 * (например, у статики или ошибочных URL без активной сессии).
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;

        model.addAttribute("message", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));

        if (status == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        } else if (status == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        }
        return "error/500";
    }
}
