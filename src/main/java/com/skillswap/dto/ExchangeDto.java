package com.skillswap.dto;

import java.time.LocalDateTime;

public record ExchangeDto(
        Long id,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        String notes,
        LocalDateTime completedAt,
        Long exchangeRequestId,
        String offerTitle,
        String requesterUsername,
        String ownerUsername
) {}
