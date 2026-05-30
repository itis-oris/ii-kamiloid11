package com.skillswap.repository;

import com.skillswap.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторий отзывов. Помимо стандартных найти/сохранить отдаёт
 * средний рейтинг пользователя через aggregate-запрос с COALESCE,
 * чтобы у нового пользователя без отзывов возвращался 0, а не null.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTargetId(Long targetId);

    List<Review> findByExchangeId(Long exchangeId);

    boolean existsByExchangeIdAndAuthorId(Long exchangeId, Long authorId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.target.id = :userId")
    Double findAverageRatingByUserId(@Param("userId") Long userId);
}
