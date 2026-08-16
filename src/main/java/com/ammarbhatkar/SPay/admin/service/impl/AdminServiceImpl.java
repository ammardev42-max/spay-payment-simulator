package com.ammarbhatkar.SPay.admin.service.impl;

import com.ammarbhatkar.SPay.admin.dto.response.DlqEventResponse;
import com.ammarbhatkar.SPay.admin.service.AdminService;
import com.ammarbhatkar.SPay.payment.entity.DlqEvent;
import com.ammarbhatkar.SPay.payment.repository.DlqEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final DlqEventRepository dlqEventRepository;

    @Override
    public List<DlqEventResponse> getDlqEvents() {
        return dlqEventRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DlqEventResponse toResponse(DlqEvent dlqEvent) {
        return new DlqEventResponse(
                dlqEvent.getId(),
                dlqEvent.getTransaction().getId(),
                dlqEvent.getLastAttempt() == null ? null : dlqEvent.getLastAttempt().getId(),
                dlqEvent.getStatus().name(),
                dlqEvent.getReasonCode(),
                dlqEvent.getReason(),
                dlqEvent.getRetryCount(),
                dlqEvent.getCreatedAt(),
                dlqEvent.getLastRetriedAt()
        );
    }
}
