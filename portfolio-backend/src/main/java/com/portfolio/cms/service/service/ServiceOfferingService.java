package com.portfolio.cms.service.service;

import com.portfolio.cms.service.dto.ServiceDto;
import com.portfolio.cms.service.dto.ServiceRequest;

import java.util.List;

public interface ServiceOfferingService {
    List<ServiceDto> getActiveServices();
    List<ServiceDto> getAllServicesAdmin();
    ServiceDto getServiceById(Long id);
    ServiceDto createService(ServiceRequest request, Long userId, String userEmail);
    ServiceDto updateService(Long id, ServiceRequest request, Long userId, String userEmail);
    void deleteService(Long id, Long userId, String userEmail);
}
