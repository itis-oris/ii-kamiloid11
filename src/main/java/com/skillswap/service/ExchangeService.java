package com.skillswap.service;

import com.skillswap.dto.ExchangeDto;
import com.skillswap.dto.ExchangeRequestDto;
import com.skillswap.entity.*;
import com.skillswap.exception.AccessDeniedException;
import com.skillswap.exception.ExchangeRequestNotFoundException;
import com.skillswap.repository.ExchangeRepository;
import com.skillswap.repository.ExchangeRequestRepository;
import com.skillswap.repository.SkillOfferRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeRequestRepository requestRepository;
    private final ExchangeRepository exchangeRepository;
    private final SkillOfferRepository offerRepository;
    private final UserRepository userRepository;

    private static final int DEFAULT_SDVIG_DNEY = 1;

    @Transactional
    public ExchangeRequest createRequest(Long offerId, String message, String username) {
        User zayavitel = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        SkillOffer celevoeObyavlenie = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Объявление не найдено"));

        if (celevoeObyavlenie.getOwner().getId().equals(zayavitel.getId())) {
            throw new AccessDeniedException("Нельзя отправить заявку на собственное объявление");
        }
        if (requestRepository.existsByOfferIdAndRequesterId(offerId, zayavitel.getId())) {
            throw new AccessDeniedException("По этому объявлению у вас уже есть заявка в работе");
        }

        ExchangeRequest novayaZayavka = new ExchangeRequest();
        novayaZayavka.setOffer(celevoeObyavlenie);
        novayaZayavka.setRequester(zayavitel);
        novayaZayavka.setMessage(message);
        return requestRepository.save(novayaZayavka);
    }

    @Transactional
    public void acceptRequest(Long requestId, String username) {
        ExchangeRequest zayavka = getRequestEntity(requestId);
        validateOfferOwner(zayavka, username);

        zayavka.setStatus(RequestStatus.ACCEPTED);
        zayavka.setRespondedAt(LocalDateTime.now());
        requestRepository.save(zayavka);

        Exchange noviyObmen = new Exchange();
        noviyObmen.setExchangeRequest(zayavka);
        noviyObmen.setScheduledAt(LocalDateTime.now().plusDays(DEFAULT_SDVIG_DNEY));
        noviyObmen.setDurationMinutes((int) (zayavka.getOffer().getHoursPerSession() * 60));
        exchangeRepository.save(noviyObmen);
    }

    @Transactional
    public void rejectRequest(Long requestId, String username) {
        ExchangeRequest zayavka = getRequestEntity(requestId);
        validateOfferOwner(zayavka, username);

        zayavka.setStatus(RequestStatus.REJECTED);
        zayavka.setRespondedAt(LocalDateTime.now());
        requestRepository.save(zayavka);
    }

    @Transactional
    public void completeExchange(Long exchangeId, String notes, String username) {
        Exchange obmen = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Обмен не найден"));

        String loginVladeltsa = obmen.getExchangeRequest().getOffer().getOwner().getUsername();
        if (!loginVladeltsa.equals(username)) {
            throw new AccessDeniedException("Завершить обмен может только владелец объявления");
        }

        obmen.setCompletedAt(LocalDateTime.now());
        obmen.setNotes(notes);
        obmen.getExchangeRequest().setStatus(RequestStatus.COMPLETED);
        exchangeRepository.save(obmen);
    }

    public List<ExchangeRequestDto> getIncomingRequests(String username) {
        User vladelets = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        List<ExchangeRequest> nayedennye = requestRepository.findByOfferOwnerId(vladelets.getId());
        List<ExchangeRequestDto> rezultat = new ArrayList<>(nayedennye.size());
        for (ExchangeRequest req : nayedennye) {
            rezultat.add(toRequestDto(req));
        }
        return rezultat;
    }

    public List<ExchangeRequestDto> getOutgoingRequests(String username) {
        User zayavitel = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return requestRepository.findByRequesterId(zayavitel.getId())
                .stream().map(this::toRequestDto).toList();
    }

    public List<ExchangeDto> getUserExchanges(String username) {
        User uchastnik = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return exchangeRepository
                .findByExchangeRequestRequesterIdOrExchangeRequestOfferOwnerId(uchastnik.getId(), uchastnik.getId())
                .stream().map(this::toExchangeDto).toList();
    }

    public ExchangeDto getExchangeById(Long id) {
        Exchange obmen = exchangeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Обмен не найден"));
        return toExchangeDto(obmen);
    }

    public Exchange getExchangeEntityById(Long id) {
        Exchange obmen = exchangeRepository.findById(id).orElse(null);
        if (obmen == null) {
            throw new RuntimeException("Обмен не найден");
        }
        return obmen;
    }

    private ExchangeRequest getRequestEntity(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Заявка на обмен не найдена: {}", id);
                    return new ExchangeRequestNotFoundException("Заявка на обмен не найдена: " + id);
                });
    }

    private void validateOfferOwner(ExchangeRequest request, String username) {
        if (!request.getOffer().getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Управлять заявкой может только владелец объявления");
        }
    }

    private ExchangeRequestDto toRequestDto(ExchangeRequest req) {
        return new ExchangeRequestDto(
                req.getId(), req.getMessage(), req.getStatus().name(),
                req.getCreatedAt(), req.getRespondedAt(),
                req.getRequester().getUsername(), req.getRequester().getId(),
                req.getOffer().getId(), req.getOffer().getTitle()
        );
    }

    private ExchangeDto toExchangeDto(Exchange e) {
        ExchangeRequest req = e.getExchangeRequest();
        return new ExchangeDto(
                e.getId(), e.getScheduledAt(), e.getDurationMinutes(),
                e.getNotes(), e.getCompletedAt(),
                req.getId(), req.getOffer().getTitle(),
                req.getRequester().getUsername(),
                req.getOffer().getOwner().getUsername()
        );
    }
}
