package com.Siddhant.UserApp.Service;

import com.Siddhant.UserApp.dto.SupportRequestRequest;
import com.Siddhant.UserApp.dto.SupportRequestResponse;
import com.Siddhant.UserApp.enums.SupportRequestStatus;

import java.util.List;
import java.util.UUID;

public interface SupportRequestService {
    SupportRequestResponse createSupportRequest(SupportRequestRequest request);
    List<SupportRequestResponse> getMySupportRequests();
    SupportRequestResponse getMySupportRequestById(UUID id);
    
    // Admin endpoints
    List<SupportRequestResponse> getAllSupportRequests();
    SupportRequestResponse getSupportRequestById(UUID id);
    SupportRequestResponse updateSupportRequestStatus(UUID id, SupportRequestStatus status);
}
