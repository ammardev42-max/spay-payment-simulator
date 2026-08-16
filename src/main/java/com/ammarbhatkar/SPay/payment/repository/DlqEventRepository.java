package com.ammarbhatkar.SPay.payment.repository;

import com.ammarbhatkar.SPay.payment.entity.DlqEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DlqEventRepository extends JpaRepository<DlqEvent, UUID> {

    List<DlqEvent> findAllByOrderByCreatedAtDesc();
}
