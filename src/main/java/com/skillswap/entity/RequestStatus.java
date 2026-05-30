package com.skillswap.entity;

/** Состояние заявки на обмен: PENDING → ACCEPTED|REJECTED, ACCEPTED → COMPLETED после завершения. */
public enum RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED
}
