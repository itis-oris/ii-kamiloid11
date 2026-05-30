package com.skillswap.controller;

import com.skillswap.dto.SkillOfferDto;
import com.skillswap.entity.SkillCategory;
import com.skillswap.entity.SkillLevel;
import com.skillswap.entity.SkillOffer;
import com.skillswap.form.OfferForm;
import com.skillswap.repository.SkillRepository;
import com.skillswap.service.SkillOfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * MVC-контроллер для CRUD-а объявлений {@code SkillOffer}.
 * Маршрутизация и привязка форм — здесь, бизнес-логика — в {@link SkillOfferService}.
 */
@Controller
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final SkillOfferService offerService;
    private final SkillRepository skillRepository;

    // Размер одной страницы каталога. // TODO вынести в конфиг потом
    private static final int DEFAULT_RAZMER_STRANITSY = 9;

    /** Список активных объявлений с пагинацией. Сортируем по дате создания (новые сверху). */
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "9") int size,
                       Model model) {
        Page<SkillOfferDto> stranitsaObyavleniy = offerService.findAllActive(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        model.addAttribute("offers", stranitsaObyavleniy);
        model.addAttribute("categories", SkillCategory.values());
        model.addAttribute("levels", SkillLevel.values());
        return "offers/list";
    }

    /** Страница деталей конкретного объявления. */
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        SkillOfferDto karta = offerService.getOfferById(id);
        model.addAttribute("offer", karta);
        return "offers/detail";
    }

    /** Форма создания нового объявления. */
    @GetMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createForm(Model model) {
        model.addAttribute("form", new OfferForm());
        model.addAttribute("skills", skillRepository.findAll());
        model.addAttribute("editMode", false);
        return "offers/form";
    }

    /** Обработка POST-а формы создания: валидация → сохранение → редирект на детальную. */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String create(@Valid @ModelAttribute("form") OfferForm form,
                         BindingResult result,
                         Model model,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            // TODO дедуплицировать с update — пока оставляю как есть, проще читать
            model.addAttribute("skills", skillRepository.findAll());
            model.addAttribute("editMode", false);
            return "offers/form";
        }
        SkillOffer sohranennoeObyavlenie = offerService.createOffer(form, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Объявление успешно создано!");
        return "redirect:/offers/" + sohranennoeObyavlenie.getId();
    }

    /** Форма редактирования: открывается только владельцу объявления. */
    @GetMapping("/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        SkillOffer obyavlenie = offerService.getEntityById(id);
        if (!obyavlenie.getOwner().getUsername().equals(principal.getName())) {
            return "redirect:/offers/" + id;
        }
        model.addAttribute("form", offerService.toForm(obyavlenie));
        model.addAttribute("skills", skillRepository.findAll());
        model.addAttribute("editMode", true);
        model.addAttribute("offerId", id);
        return "offers/form";
    }

    /** Обработка POST-а формы редактирования. */
    @PostMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") OfferForm form,
                         BindingResult result,
                         Model model,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("skills", skillRepository.findAll());
            model.addAttribute("editMode", true);
            model.addAttribute("offerId", id);
            return "offers/form";
        }
        offerService.updateOffer(id, form, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Объявление обновлено!");
        return "redirect:/offers/" + id;
    }

    /** Soft-delete объявления через POST-форму (CSRF-safe), доступен только владельцу. */
    @PostMapping("/{id}/delete")
    @PreAuthorize("isAuthenticated()")
    public String delete(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        offerService.deleteOffer(id, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Объявление удалено.");
        return "redirect:/offers";
    }
}
