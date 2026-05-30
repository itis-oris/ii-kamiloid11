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

/**
 * Бизнес-логика заявок на обмен и состоявшихся обменов.
 * Тут живёт принятие/отклонение заявок, фиксация обмена и подбор списков для дашборда пользователя.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final ExchangeRequestRepository requestRepository;
    private final ExchangeRepository exchangeRepository;
    private final SkillOfferRepository offerRepository;
    private final UserRepository userRepository;

    // дефолтное время через сколько суток считаем встречу запланированной
    // TODO вынести в конфиг потом
    private static final int DEFAULT_SDVIG_DNEY = 1;

    /**
     * Создаёт заявку на обмен от текущего пользователя к владельцу объявления.
     * Запрещает самозаявки и дубли pending-заявок по одному объявлению.
     */
    @Transactional
    public ExchangeRequest createRequest(Long offerId, String message, String username) {
        // System.out.println("debug createRequest offer=" + offerId);
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

    /**
     * Помечает заявку как принятую и сразу заводит карточку обмена со стандартным
     * сдвигом на сутки вперёд. Само время встречи можно править руками позже.
     */
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

    /** Помечает заявку как отклонённую. Создавать обмен здесь не надо. */
    @Transactional
    public void rejectRequest(Long requestId, String username) {
        ExchangeRequest zayavka = getRequestEntity(requestId);
        validateOfferOwner(zayavka, username);

        zayavka.setStatus(RequestStatus.REJECTED);
        zayavka.setRespondedAt(LocalDateTime.now());
        requestRepository.save(zayavka);
    }

    /**
     * Закрывает обмен: проставляет дату завершения, переводит заявку в статус COMPLETED.
     * После этого станет доступна форма отзыва.
     */
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

    /** Входящие заявки на объявления текущего пользователя. */
    public List<ExchangeRequestDto> getIncomingRequests(String username) {
        User vladelets = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        // обычный цикл вместо стрима — чуть проще читать в дебагере
        List<ExchangeRequest> nayedennye = requestRepository.findByOfferOwnerId(vladelets.getId());
        List<ExchangeRequestDto> rezultat = new ArrayList<>(nayedennye.size());
        for (ExchangeRequest req : nayedennye) {
            rezultat.add(toRequestDto(req));
        }
        return rezultat;
    }

    /** Заявки, которые пользователь сам отправил по чужим объявлениям. */
    public List<ExchangeRequestDto> getOutgoingRequests(String username) {
        User zayavitel = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return requestRepository.findByRequesterId(zayavitel.getId())
                .stream().map(this::toRequestDto).toList();
    }

    /** Все обмены, в которых пользователь — либо автор, либо заявитель. */
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
        // здесь старый стиль через if — переписывать в orElseThrow не стал, не критично
        Exchange obmen = exchangeRepository.findById(id).orElse(null);
        if (obmen == null) {
            throw new RuntimeException("Обмен не найден");
        }
        // if (obmen.getCompletedAt() != null) { ... } — раньше тут была проверка завершённости,
        // вынес в ReviewService, но удалять пока не стал, мало ли пригодится
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
