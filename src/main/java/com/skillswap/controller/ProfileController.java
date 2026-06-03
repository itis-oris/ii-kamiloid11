package com.skillswap.controller;

import com.skillswap.dto.ReviewDto;
import com.skillswap.dto.SkillOfferDto;
import com.skillswap.dto.UserDto;
import com.skillswap.entity.User;
import com.skillswap.form.ProfileForm;
import com.skillswap.service.ReviewService;
import com.skillswap.service.SkillOfferService;
import com.skillswap.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final SkillOfferService offerService;
    private final ReviewService reviewService;

    @GetMapping("/{username}")
    public String viewProfile(@PathVariable String username, Model model) {
        User uchastnik = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        UserDto profilDto = userService.toDto(uchastnik);
        List<SkillOfferDto> obyavleniyaPolzovatelya = offerService.findByOwner(username);
        List<ReviewDto> otzyvy = reviewService.getReviewsForUser(uchastnik.getId());

        model.addAttribute("user", profilDto);
        model.addAttribute("offers", obyavleniyaPolzovatelya);
        model.addAttribute("reviews", otzyvy);
        return "profile/view";
    }

    @GetMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String editForm(Model model, Principal principal) {
        User profilTekushchego = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        ProfileForm forma = new ProfileForm();
        forma.setFirstName(profilTekushchego.getFirstName());
        forma.setLastName(profilTekushchego.getLastName());
        forma.setBio(profilTekushchego.getBio());
        forma.setAvatarUrl(profilTekushchego.getAvatarUrl());
        model.addAttribute("form", forma);
        return "profile/edit";
    }

    @PostMapping("/edit")
    @PreAuthorize("isAuthenticated()")
    public String updateProfile(@Valid @ModelAttribute("form") ProfileForm form,
                                BindingResult result,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "profile/edit";
        }
        userService.updateProfile(principal.getName(), form);
        redirectAttributes.addFlashAttribute("success", "Профиль обновлён!");
        return "redirect:/profile/" + principal.getName();
    }
}
