package com.skillswap.controller;

import com.skillswap.dto.ExchangeDto;
import com.skillswap.entity.Exchange;
import com.skillswap.form.ReviewForm;
import com.skillswap.service.ExchangeService;
import com.skillswap.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/exchanges")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ExchangeController {

    private final ExchangeService exchangeService;
    private final ReviewService reviewService;

    @GetMapping
    public String list(Model model, Principal principal) {
        model.addAttribute("incoming", exchangeService.getIncomingRequests(principal.getName()));
        model.addAttribute("outgoing", exchangeService.getOutgoingRequests(principal.getName()));
        model.addAttribute("exchanges", exchangeService.getUserExchanges(principal.getName()));
        return "exchange/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Principal principal) {
        ExchangeDto obmenDto = exchangeService.getExchangeById(id);
        Exchange suschnost = exchangeService.getExchangeEntityById(id);
        model.addAttribute("exchange", obmenDto);
        model.addAttribute("reviews", suschnost.getReviews());
        model.addAttribute("reviewForm", new ReviewForm());
        model.addAttribute("currentUser", principal.getName());

        Long idVladeltsa = suschnost.getExchangeRequest().getOffer().getOwner().getId();
        Long idZayavitelya = suschnost.getExchangeRequest().getRequester().getId();
        String loginVladeltsa = suschnost.getExchangeRequest().getOffer().getOwner().getUsername();
        model.addAttribute("targetUserId",
                principal.getName().equals(loginVladeltsa) ? idZayavitelya : idVladeltsa);

        return "exchange/detail";
    }

    @PostMapping("/request/{requestId}/accept")
    public String acceptRequest(@PathVariable Long requestId, Principal principal,
                                RedirectAttributes redirectAttributes) {
        exchangeService.acceptRequest(requestId, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Заявка принята!");
        return "redirect:/exchanges";
    }

    @PostMapping("/request/{requestId}/reject")
    public String rejectRequest(@PathVariable Long requestId, Principal principal,
                                RedirectAttributes redirectAttributes) {
        exchangeService.rejectRequest(requestId, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Заявка отклонена.");
        return "redirect:/exchanges";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Long id,
                           @RequestParam(required = false) String notes,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        exchangeService.completeExchange(id, notes, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Обмен отмечен как завершённый!");
        return "redirect:/exchanges/" + id;
    }

    @PostMapping("/{id}/review")
    public String review(@PathVariable Long id,
                         @RequestParam Long targetUserId,
                         @ModelAttribute ReviewForm form,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
        reviewService.createReview(id, targetUserId, form, principal.getName());
        redirectAttributes.addFlashAttribute("success", "Отзыв отправлен!");
        return "redirect:/exchanges/" + id;
    }
}
