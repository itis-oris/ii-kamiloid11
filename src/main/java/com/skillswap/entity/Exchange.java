package com.skillswap.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exchanges")
@Getter
@Setter
@NoArgsConstructor
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exchange_request_id", nullable = false, unique = true)
    private ExchangeRequest exchangeRequest;

    @OneToMany(mappedBy = "exchange")
    private List<Review> reviews = new ArrayList<>();
}
