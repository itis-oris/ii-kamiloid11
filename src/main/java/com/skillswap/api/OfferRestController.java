package com.skillswap.api;

import com.skillswap.dto.SkillOfferDto;
import com.skillswap.entity.SkillCategory;
import com.skillswap.entity.SkillLevel;
import com.skillswap.entity.SkillOffer;
import com.skillswap.form.OfferForm;
import com.skillswap.request.OfferRequest;
import com.skillswap.service.SkillOfferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST-обёртка над сервисом объявлений. Используется фронтом для AJAX-поиска
 * и сторонними клиентами (документация — Swagger UI). Сессионная авторизация:
 * операции записи требуют залогиненного пользователя.
 */
@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
@Tag(name = "Skill Offers", description = "REST API объявлений о навыках")
public class OfferRestController {

    private final SkillOfferService offerService;

    /**
     * Возвращает страницу активных объявлений. Если задан хотя бы один фильтр,
     * переключаемся на CriteriaBuilder-поиск и оборачиваем результат в PageImpl,
     * чтобы клиенту не пришлось ветвить два формата ответа.
     */
    @GetMapping
    @Operation(summary = "Получить активные объявления",
            description = "Возвращает постраничный список активных предложений навыков")
    @ApiResponse(responseCode = "200", description = "Список получен")
    public ResponseEntity<Page<SkillOfferDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) SkillCategory category,
            @RequestParam(required = false) SkillLevel level) {

        if (keyword != null || category != null || level != null) {
            List<SkillOfferDto> rezultatyPoiska = offerService.searchOffers(keyword, category, level);
            return ResponseEntity.ok(new org.springframework.data.domain.PageImpl<>(rezultatyPoiska));
        }

        Page<SkillOfferDto> stranitsa = offerService.findAllActive(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
        return ResponseEntity.ok(stranitsa);
    }

    /** Получить одно объявление по id. */
    @GetMapping("/{id}")
    @Operation(summary = "Получить объявление по ID")
    @ApiResponse(responseCode = "200", description = "Объявление найдено")
    @ApiResponse(responseCode = "404", description = "Объявление не найдено")
    public ResponseEntity<SkillOfferDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    /** Создать объявление. Автор — текущий аутентифицированный пользователь. */
    @PostMapping
    @Operation(summary = "Создать объявление")
    @ApiResponse(responseCode = "201", description = "Объявление создано")
    public ResponseEntity<SkillOfferDto> create(@Valid @RequestBody OfferRequest request,
                                                Principal principal) {
        OfferForm forma = toForm(request);
        SkillOffer sozdannoeObyavlenie = offerService.createOffer(forma, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.toDto(sozdannoeObyavlenie));
    }

    /** Обновить объявление. Только владелец. */
    @PutMapping("/{id}")
    @Operation(summary = "Обновить объявление")
    @ApiResponse(responseCode = "200", description = "Объявление обновлено")
    public ResponseEntity<SkillOfferDto> update(@PathVariable Long id,
                                                @Valid @RequestBody OfferRequest request,
                                                Principal principal) {
        OfferForm forma = toForm(request);
        SkillOffer obnovlennoe = offerService.updateOffer(id, forma, principal.getName());
        return ResponseEntity.ok(offerService.toDto(obnovlennoe));
    }

    /** Удалить объявление. Только владелец. */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить объявление")
    @ApiResponse(responseCode = "204", description = "Объявление удалено")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        offerService.deleteOffer(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    /** Конвертер REST-запроса во внутреннюю форму, чтобы не дублировать поля. */
    private OfferForm toForm(OfferRequest request) {
        OfferForm forma = new OfferForm();
        forma.setTitle(request.getTitle());
        forma.setDescription(request.getDescription());
        forma.setHoursPerSession(request.getHoursPerSession());
        forma.setHourlyRate(request.getHourlyRate());
        forma.setRateCurrency(request.getRateCurrency());
        forma.setMaxStudents(request.getMaxStudents());
        forma.setSkillId(request.getSkillId());
        return forma;
    }
}
