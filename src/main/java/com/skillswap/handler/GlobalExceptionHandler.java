package com.skillswap.handler;

import com.skillswap.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Глобальный обработчик исключений. Для обычных запросов отдаёт HTML-страницы 404/403/500,
 * для AJAX (определяем по X-Requested-With или Accept: application/json) — JSON-ответ
 * с {error, message, timestamp}. Это позволяет фронту не показывать в модалках страничку с шаблоном.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SkillOfferNotFoundException.class)
    public Object handleNotFound(SkillOfferNotFoundException ex, HttpServletRequest request) {
        log.error("Exception caught: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return jsonError(HttpStatus.NOT_FOUND, ex.getMessage());
        }
        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(ExchangeRequestNotFoundException.class)
    public Object handleExchangeNotFound(ExchangeRequestNotFoundException ex, HttpServletRequest request) {
        log.error("Exception caught: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return jsonError(HttpStatus.NOT_FOUND, ex.getMessage());
        }
        ModelAndView mav = new ModelAndView("error/404");
        mav.setStatus(HttpStatus.NOT_FOUND);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.error("Exception caught: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return jsonError(HttpStatus.FORBIDDEN, ex.getMessage());
        }
        ModelAndView mav = new ModelAndView("error/403");
        mav.setStatus(HttpStatus.FORBIDDEN);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Object handleUserExists(UserAlreadyExistsException ex, HttpServletRequest request) {
        log.error("Exception caught: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return jsonError(HttpStatus.CONFLICT, ex.getMessage());
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.CONFLICT);
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(ExternalApiException.class)
    public Object handleExternalApi(ExternalApiException ex, HttpServletRequest request) {
        log.error("Exception caught: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return jsonError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
        mav.addObject("message", "Внешний сервис временно недоступен");
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception caught: {}", ex.getMessage(), ex);
        if (isAjax(request)) {
            return jsonError(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла непредвиденная ошибка");
        }
        ModelAndView mav = new ModelAndView("error/500");
        mav.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        mav.addObject("message", "Произошла непредвиденная ошибка");
        return mav;
    }

    private boolean isAjax(HttpServletRequest request) {
        String xRequestedWith = request.getHeader("X-Requested-With");
        String accept = request.getHeader("Accept");
        return "XMLHttpRequest".equals(xRequestedWith)
                || (accept != null && accept.contains("application/json"));
    }

    private ResponseEntity<Map<String, Object>> jsonError(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "error", status.getReasonPhrase(),
                "message", message,
                "timestamp", LocalDateTime.now().toString()
        ));
    }
}
