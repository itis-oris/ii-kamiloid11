package com.skillswap.service;

import com.skillswap.dto.SkillOfferDto;
import com.skillswap.entity.*;
import com.skillswap.exception.AccessDeniedException;
import com.skillswap.exception.SkillOfferNotFoundException;
import com.skillswap.form.OfferForm;
import com.skillswap.repository.ReviewRepository;
import com.skillswap.repository.SkillOfferRepository;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillOfferService {

    private final SkillOfferRepository offerRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final EntityManager entityManager;

    public Page<SkillOfferDto> findAllActive(Pageable pageable) {
        return offerRepository.findByIsActiveTrue(pageable).map(this::toDto);
    }

    public SkillOfferDto getOfferById(Long id) {
        SkillOffer naydennoeObyavlenie = offerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Объявление не найдено: {}", id);
                    return new SkillOfferNotFoundException("Объявление не найдено по id: " + id);
                });
        return toDto(naydennoeObyavlenie);
    }

    public SkillOffer getEntityById(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new SkillOfferNotFoundException("Объявление не найдено по id: " + id));
    }

    @Transactional
    public SkillOffer createOffer(OfferForm form, String username) {
        User vladelets = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Skill vybrannyyNavyk = skillRepository.findById(form.getSkillId())
                .orElseThrow(() -> new RuntimeException("Навык не найден"));

        SkillOffer chernovikObyavleniya = new SkillOffer();
        chernovikObyavleniya.setTitle(form.getTitle());
        chernovikObyavleniya.setDescription(form.getDescription());
        chernovikObyavleniya.setHoursPerSession(form.getHoursPerSession());
        chernovikObyavleniya.setHourlyRate(form.getHourlyRate());
        chernovikObyavleniya.setRateCurrency(form.getRateCurrency() != null ? form.getRateCurrency() : "EUR");
        chernovikObyavleniya.setMaxStudents(form.getMaxStudents());
        chernovikObyavleniya.setOwner(vladelets);
        chernovikObyavleniya.setSkill(vybrannyyNavyk);

        return offerRepository.save(chernovikObyavleniya);
    }

    @Transactional
    public SkillOffer updateOffer(Long id, OfferForm form, String username) {
        SkillOffer redaktiruemoeObyavlenie = getEntityById(id);
        if (!redaktiruemoeObyavlenie.getOwner().getUsername().equals(username)) {
            log.warn("Пользователь {} попытался отредактировать объявление {} владельца {}",
                    username, id, redaktiruemoeObyavlenie.getOwner().getUsername());
            throw new AccessDeniedException("Редактировать можно только свои объявления");
        }
        Skill vybrannyyNavyk = skillRepository.findById(form.getSkillId())
                .orElseThrow(() -> new RuntimeException("Навык не найден"));

        redaktiruemoeObyavlenie.setTitle(form.getTitle());
        redaktiruemoeObyavlenie.setDescription(form.getDescription());
        redaktiruemoeObyavlenie.setHoursPerSession(form.getHoursPerSession());
        redaktiruemoeObyavlenie.setHourlyRate(form.getHourlyRate());
        redaktiruemoeObyavlenie.setRateCurrency(form.getRateCurrency() != null ? form.getRateCurrency() : "EUR");
        redaktiruemoeObyavlenie.setMaxStudents(form.getMaxStudents());
        redaktiruemoeObyavlenie.setSkill(vybrannyyNavyk);

        return offerRepository.save(redaktiruemoeObyavlenie);
    }

    @Transactional
    public void deleteOffer(Long id, String username) {
        SkillOffer obyavlenieKUdaleniyu = getEntityById(id);
        if (!obyavlenieKUdaleniyu.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Удалять можно только свои объявления");
        }
        offerRepository.delete(obyavlenieKUdaleniyu);
    }

    public List<SkillOfferDto> findByOwner(String username) {
        User vladelets = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return offerRepository.findByOwnerIdAndIsActiveTrue(vladelets.getId())
                .stream().map(this::toDto).toList();
    }

    public List<SkillOfferDto> findByCategoryAndMinRating(SkillCategory category, double minRating) {
        return offerRepository.findActiveBySkillCategoryAndMinOwnerRating(category, minRating)
                .stream().map(this::toDto).toList();
    }

    @SuppressWarnings("unchecked")
    public List<SkillOfferDto> searchOffers(String keyword, SkillCategory category, SkillLevel level) {
        CriteriaBuilder konstruktor = entityManager.getCriteriaBuilder();
        CriteriaQuery<SkillOffer> zapros = konstruktor.createQuery(SkillOffer.class);
        Root<SkillOffer> koren = zapros.from(SkillOffer.class);
        Join<SkillOffer, Skill> soedinenieNavykov = (Join<SkillOffer, Skill>) (Join<?, ?>) koren.fetch("skill", JoinType.LEFT);
        koren.fetch("owner", JoinType.LEFT);

        List<Predicate> usloviya = new ArrayList<>();
        usloviya.add(konstruktor.isTrue(koren.get("isActive")));

        if (keyword != null && !keyword.isBlank()) {
            String shablon = "%" + keyword.toLowerCase() + "%";
            Predicate sovpadenieVZagolovke = konstruktor.like(konstruktor.lower(koren.get("title")), shablon);
            Predicate sovpadenieVOpisanii = konstruktor.like(konstruktor.lower(koren.get("description")), shablon);
            usloviya.add(konstruktor.or(sovpadenieVZagolovke, sovpadenieVOpisanii));
        }

        if (category != null) {
            usloviya.add(konstruktor.equal(soedinenieNavykov.get("category"), category));
        }

        if (level != null) {
            usloviya.add(konstruktor.equal(soedinenieNavykov.get("level"), level));
        }

        zapros.where(usloviya.toArray(new Predicate[0]));
        zapros.orderBy(konstruktor.desc(koren.get("createdAt")));

        return entityManager.createQuery(zapros).getResultList()
                .stream().map(this::toDto).toList();
    }

    public SkillOfferDto toDto(SkillOffer offer) {
        Double reytingVladeltsa = reviewRepository.findAverageRatingByUserId(offer.getOwner().getId());
        return new SkillOfferDto(
                offer.getId(),
                offer.getTitle(),
                offer.getDescription(),
                offer.getHoursPerSession(),
                offer.getHourlyRate(),
                offer.getRateCurrency(),
                offer.getMaxStudents(),
                offer.getIsActive(),
                offer.getCreatedAt(),
                offer.getUpdatedAt(),
                offer.getOwner().getUsername(),
                offer.getOwner().getId(),
                reytingVladeltsa,
                offer.getSkill().getTitle(),
                offer.getSkill().getCategory().name(),
                offer.getSkill().getLevel().name()
        );
    }

    public OfferForm toForm(SkillOffer offer) {
        OfferForm forma = new OfferForm();
        forma.setTitle(offer.getTitle());
        forma.setDescription(offer.getDescription());
        forma.setHoursPerSession(offer.getHoursPerSession());
        forma.setHourlyRate(offer.getHourlyRate());
        forma.setRateCurrency(offer.getRateCurrency());
        forma.setMaxStudents(offer.getMaxStudents());
        forma.setSkillId(offer.getSkill().getId());
        return forma;
    }
}
