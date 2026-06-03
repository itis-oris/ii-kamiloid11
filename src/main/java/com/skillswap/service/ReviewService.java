package com.skillswap.service;

import com.skillswap.dto.ReviewDto;
import com.skillswap.entity.Exchange;
import com.skillswap.entity.Review;
import com.skillswap.entity.User;
import com.skillswap.exception.AccessDeniedException;
import com.skillswap.form.ReviewForm;
import com.skillswap.repository.ExchangeRepository;
import com.skillswap.repository.ReviewRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Review createReview(Long exchangeId, Long targetUserId, ReviewForm form, String authorUsername) {
        User avtor = userRepository.findByUsername(authorUsername)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        User adresat = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("Целевой пользователь не найден"));
        Exchange obmen = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Обмен не найден"));

        if (obmen.getCompletedAt() == null) {
            throw new AccessDeniedException("Нельзя оставить отзыв на незавершённый обмен");
        }
        if (reviewRepository.existsByExchangeIdAndAuthorId(exchangeId, avtor.getId())) {
            throw new AccessDeniedException("Вы уже оставляли отзыв на этот обмен");
        }

        String loginZayavitelya = obmen.getExchangeRequest().getRequester().getUsername();
        String loginVladeltsa = obmen.getExchangeRequest().getOffer().getOwner().getUsername();
        if (!authorUsername.equals(loginZayavitelya) && !authorUsername.equals(loginVladeltsa)) {
            throw new AccessDeniedException("Вы не являетесь участником этого обмена");
        }

        Review novyyOtzyv = new Review();
        novyyOtzyv.setRating(form.getRating());
        novyyOtzyv.setComment(form.getComment());
        novyyOtzyv.setExchange(obmen);
        novyyOtzyv.setAuthor(avtor);
        novyyOtzyv.setTarget(adresat);

        return reviewRepository.save(novyyOtzyv);
    }

    public List<ReviewDto> getReviewsForUser(Long userId) {
        return reviewRepository.findByTargetId(userId)
                .stream().map(this::toDto).toList();
    }

    public Double getAverageRating(Long userId) {
        return reviewRepository.findAverageRatingByUserId(userId);
    }

    private ReviewDto toDto(Review r) {
        return new ReviewDto(
                r.getId(), r.getRating(), r.getComment(), r.getCreatedAt(),
                r.getAuthor().getUsername(), r.getTarget().getUsername(),
                r.getExchange().getId()
        );
    }
}
