package com.skillswap.repository;

import com.skillswap.entity.ExchangeRequest;
import com.skillswap.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Репозиторий заявок на обмен — двунаправленные выборки (для заявителя и для владельца). */
public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    List<ExchangeRequest> findByRequesterId(Long requesterId);

    List<ExchangeRequest> findByOfferOwnerIdAndStatus(Long ownerId, RequestStatus status);

    List<ExchangeRequest> findByOfferOwnerId(Long ownerId);

    boolean existsByOfferIdAndRequesterId(Long offerId, Long requesterId);
}
