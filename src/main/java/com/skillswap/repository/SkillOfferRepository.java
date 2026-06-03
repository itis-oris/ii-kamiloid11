package com.skillswap.repository;

import com.skillswap.entity.SkillCategory;
import com.skillswap.entity.SkillOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SkillOfferRepository extends JpaRepository<SkillOffer, Long> {

    Page<SkillOffer> findByIsActiveTrue(Pageable pageable);

    List<SkillOffer> findByOwnerIdAndIsActiveTrue(Long ownerId);

    @Query("""
            SELECT o FROM SkillOffer o
            WHERE o.skill.category = :category
            AND (SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.target = o.owner) >= :minRating
            AND o.isActive = true
            ORDER BY o.createdAt DESC
            """)
    List<SkillOffer> findActiveBySkillCategoryAndMinOwnerRating(
            @Param("category") SkillCategory category,
            @Param("minRating") double minRating
    );
}
