package com.skillswap.repository;

import com.skillswap.entity.Exchange;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    Optional<Exchange> findByExchangeRequestId(Long exchangeRequestId);

    List<Exchange> findByExchangeRequestRequesterIdOrExchangeRequestOfferOwnerId(Long requesterId, Long ownerId);
}
