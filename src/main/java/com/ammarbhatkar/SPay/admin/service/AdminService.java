package com.ammarbhatkar.SPay.admin.service;

import com.ammarbhatkar.SPay.admin.dto.response.DlqEventResponse;
import com.ammarbhatkar.SPay.admin.dto.response.OutboxEventResponse;
import com.ammarbhatkar.SPay.admin.dto.response.ProcessedEventResponse;

import java.util.List;

public interface AdminService {

    List<DlqEventResponse> getDlqEvents();

    List<OutboxEventResponse> getOutboxEvents();

    List<ProcessedEventResponse> getProcessedEvents();
}
