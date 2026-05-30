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

/**
 * Сервис, отвечающий за CRUD объявлений-предложений навыков и за динамический поиск.
 * Поиск собран на CriteriaBuilder — это позволяет накидывать предикаты по фильтрам
 * без склейки строк JPQL.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SkillOfferService {

    private final SkillOfferRepository offerRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final EntityManager entityManager;

    /** Постраничный список активных объявлений. Используется на главной /offers. */
    public Page<SkillOfferDto> findAllActive(Pageable pageable) {
        return offerRepository.findByIsActiveTrue(pageable).map(this::toDto);
    }

    /**
     * Возвращает объявление в виде DTO. Логирует промах кэша/БД, чтобы потом
     * можно было понять, кто пытался достучаться до удалённых записей.
     */
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

    /** Создаёт новое объявление от имени указанного владельца. */
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

    /**
     * Обновляет поля существующего объявления. Перед сохранением проверяем,
     * что владелец действительно тот, кто пришёл с формой — иначе режем доступ.
     */
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

    /** Удаляет объявление. Удалять чужое нельзя — кидаем {@link AccessDeniedException}. */
    @Transactional
    public void deleteOffer(Long id, String username) {
        SkillOffer obyavlenieKUdaleniyu = getEntityById(id);
        if (!obyavlenieKUdaleniyu.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("Удалять можно только свои объявления");
        }
        offerRepository.delete(obyavlenieKUdaleniyu);
    }

    /** Все активные объявления указанного автора — для страницы профиля. */
    public List<SkillOfferDto> findByOwner(String username) {
        User vladelets = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return offerRepository.findByOwnerIdAndIsActiveTrue(vladelets.getId())
                .stream().map(this::toDto).toList();
    }

    /**
     * Подбор объявлений по категории навыка с фильтрацией по среднему рейтингу владельца.
     * Внутри репозиторий выполняет JPQL-запрос с подзапросом по AVG(rating).
     */
    public List<SkillOfferDto> findByCategoryAndMinRating(SkillCategory category, double minRating) {
        return offerRepository.findActiveBySkillCategoryAndMinOwnerRating(category, minRating)
                .stream().map(this::toDto).toList();
    }

    /**
     * Динамический поиск через CriteriaBuilder: keyword ищется по title/description,
     * категория и уровень — точное совпадение по связанной сущности Skill.
     */
    @SuppressWarnings("unchecked")
    // unchecked: API JPA Criteria для fetch возвращает сырой Join — без каста никуда.
    // взял отсюда: https://stackoverflow.com/questions/7351477/jpa-criteriabuilder-and-fetch
    public List<SkillOfferDto> searchOffers(String keyword, SkillCategory category, SkillLevel level) {
        // System.out.println("debug: searchOffers keyword=" + keyword);
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

    /** Конвертирует Entity в плоский DTO для рендера на страницах списка/детали. */
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

    /** Подготавливает форму для страницы редактирования из сохранённой сущности. */
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
