package com.skillswap.controller;

import com.skillswap.service.SkillOfferService;
import com.skillswap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер админ-панели. Виден только пользователям с ролью ROLE_ADMIN —
 * это закрывается через {@link PreAuthorize} на уровне класса.
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final SkillOfferService offerService;

    /** Сводная панель: счётчики пользователей и активных объявлений. */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute("offerCount", offerService.findAllActive(PageRequest.of(0, 1)).getTotalElements());
        return "admin/dashboard";
    }

    /** Таблица пользователей с возможностью блокировки/разблокировки. */
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll().stream().map(userService::toDto).toList());
        return "admin/users";
    }

    /** Инвертирует флаг isActive — заблокированный не сможет войти в систему. */
    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleActive(id);
        redirectAttributes.addFlashAttribute("success", "Статус пользователя обновлён");
        return "redirect:/admin/users";
    }
}
