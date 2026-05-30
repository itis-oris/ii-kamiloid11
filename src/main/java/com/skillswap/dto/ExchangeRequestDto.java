package com.skillswap.dto;

import java.time.LocalDateTime;

/** DTO заявки на обмен — для входящих/исходящих списков в /exchanges. */
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
