package com.skillswap.dto;

import java.time.LocalDateTime;

public record ExchangeRequestDto(
        Long id,
        String message,
        String status,
        LocalDateTime createdAt,
        LocalDateTime respondedAt,
        String requesterUsername,
        Long requesterId,
        Long offerId,
        String offerTitle
) {}
