package com.ammarbhatkar.SPay.admin.service;

import com.ammarbhatkar.SPay.admin.dto.response.DlqEventResponse;

import java.util.List;

public interface AdminService {

    List<DlqEventResponse> getDlqEvents();
}
