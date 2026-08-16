package com.ammarbhatkar.SPay.payment.repository;

import com.ammarbhatkar.SPay.payment.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, UUID> {

    Optional<IdempotencyRecord> findByOwnerUser_IdAndEndpointAndIdempotencyKey(
            UUID ownerUserId,
            String endpoint,
            String idempotencyKey
    );
}
