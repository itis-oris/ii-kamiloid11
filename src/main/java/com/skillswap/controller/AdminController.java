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

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final SkillOfferService offerService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute("offerCount", offerService.findAllActive(PageRequest.of(0, 1)).getTotalElements());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll().stream().map(userService::toDto).toList());
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggleUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleActive(id);
        redirectAttributes.addFlashAttribute("success", "Статус пользователя обновлён");
        return "redirect:/admin/users";
    }
}
