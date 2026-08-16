package com.ammarbhatkar.SPay.upi.repository;

import com.ammarbhatkar.SPay.common.enums.UpiHandleStatus;
import com.ammarbhatkar.SPay.upi.entity.UpiHandle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UpiHandleRepository extends JpaRepository<UpiHandle, UUID> {

    boolean existsByUpiId(String upiId);

    boolean existsByUser_IdAndDefaultHandleTrue(UUID userId);

    List<UpiHandle> findByUser_Id(UUID userId);

    Optional<UpiHandle> findByUpiIdAndStatus(String upiId, UpiHandleStatus status);
}