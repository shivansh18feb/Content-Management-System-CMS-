package com.portfolio.cms.service.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.security.UserPrincipal;
import com.portfolio.cms.service.dto.ServiceDto;
import com.portfolio.cms.service.dto.ServiceRequest;
import com.portfolio.cms.service.service.ServiceOfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/services")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Admin Services CMS", description = "CMS endpoints for modifying offered services")
public class ServiceAdminController {

    private final ServiceOfferingService serviceOfferingService;

    @GetMapping
    @Operation(summary = "List All Services (Admin)")
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getAllServices() {
        return ResponseEntity.ok(ApiResponse.ok(serviceOfferingService.getAllServicesAdmin()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Service by ID")
    public ResponseEntity<ApiResponse<ServiceDto>> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(serviceOfferingService.getServiceById(id)));
    }

    @PostMapping
    @Operation(summary = "Create Service Offering")
    public ResponseEntity<ApiResponse<ServiceDto>> createService(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ServiceRequest request) {
        ServiceDto created = serviceOfferingService.createService(request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Service created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Service Offering")
    public ResponseEntity<ApiResponse<ServiceDto>> updateService(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ServiceRequest request) {
        ServiceDto updated = serviceOfferingService.updateService(id, request, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Service updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Service Offering")
    public ResponseEntity<ApiResponse<Void>> deleteService(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        serviceOfferingService.deleteService(id, userPrincipal.getId(), userPrincipal.getEmail());
        return ResponseEntity.ok(ApiResponse.ok("Service deleted successfully"));
    }
}
