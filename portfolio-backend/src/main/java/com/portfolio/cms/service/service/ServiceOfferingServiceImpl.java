package com.portfolio.cms.service.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.service.dto.ServiceDto;
import com.portfolio.cms.service.dto.ServiceRequest;
import com.portfolio.cms.service.entity.ServiceOffering;
import com.portfolio.cms.service.repository.ServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceOfferingServiceImpl implements ServiceOfferingService {

    private final ServiceOfferingRepository repository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceDto> getActiveServices() {
        return repository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceDto> getAllServicesAdmin() {
        return repository.findAllByOrderByDisplayOrderAsc()
                .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceDto getServiceById(Long id) {
        ServiceOffering service = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return mapToDto(service);
    }

    @Override
    @Transactional
    public ServiceDto createService(ServiceRequest request, Long userId, String userEmail) {
        String featuresString = request.getFeatures() != null ? String.join(",", request.getFeatures()) : "";

        ServiceOffering service = ServiceOffering.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription().trim())
                .icon(request.getIcon())
                .features(featuresString)
                .displayOrder(request.getDisplayOrder())
                .active(request.isActive())
                .build();

        ServiceOffering saved = repository.save(service);
        auditLogService.log(userId, userEmail, "CREATE", "Service", String.valueOf(saved.getId()), null, "Created service: " + saved.getTitle());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ServiceDto updateService(Long id, ServiceRequest request, Long userId, String userEmail) {
        ServiceOffering service = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        String featuresString = request.getFeatures() != null ? String.join(",", request.getFeatures()) : "";

        service.setTitle(request.getTitle().trim());
        service.setDescription(request.getDescription().trim());
        service.setIcon(request.getIcon());
        service.setFeatures(featuresString);
        service.setDisplayOrder(request.getDisplayOrder());
        service.setActive(request.isActive());

        ServiceOffering saved = repository.save(service);
        auditLogService.log(userId, userEmail, "UPDATE", "Service", String.valueOf(saved.getId()), null, "Updated service: " + saved.getTitle());
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deleteService(Long id, Long userId, String userEmail) {
        ServiceOffering service = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        String title = service.getTitle();
        repository.delete(service);
        auditLogService.log(userId, userEmail, "DELETE", "Service", String.valueOf(id), null, "Deleted service: " + title);
    }

    private ServiceDto mapToDto(ServiceOffering service) {
        List<String> featureList = StringUtils.hasText(service.getFeatures()) ?
                Arrays.stream(service.getFeatures().split(","))
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .collect(Collectors.toList())
                : Collections.emptyList();

        return ServiceDto.builder()
                .id(service.getId())
                .title(service.getTitle())
                .description(service.getDescription())
                .icon(service.getIcon())
                .features(featureList)
                .displayOrder(service.getDisplayOrder())
                .active(service.isActive())
                .build();
    }
}
