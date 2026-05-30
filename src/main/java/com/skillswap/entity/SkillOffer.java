package com.skillswap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 * Сущность объявления-предложения навыка: "что я готов преподавать".
 * Связана ManyToOne с владельцем ({@link User}) и навыком ({@link Skill}),
 * а также OneToMany с заявками на обмен по этому объявлению.
 */
@Entity
@Table(name = "skill_offers")
@Getter
@Setter
@NoArgsConstructor
public class SkillOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "hours_per_session", nullable = false)
    private Double hoursPerSession = 1.0;

    @Column(name = "hourly_rate")
    private Double hourlyRate;

    @Column(name = "rate_currency", length = 3)
    private String rateCurrency = "EUR";

    @Column(name = "max_students", nullable = false)
    private Integer maxStudents = 1;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @OneToMany(mappedBy = "offer")
    private List<ExchangeRequest> exchangeRequests = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
