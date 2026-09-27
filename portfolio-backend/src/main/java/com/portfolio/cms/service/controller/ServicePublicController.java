package com.portfolio.cms.service.controller;

import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.service.dto.ServiceDto;
import com.portfolio.cms.service.service.ServiceOfferingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/services")
@RequiredArgsConstructor
@Tag(name = "Public Services", description = "Public endpoints for technical services and capabilities")
public class ServicePublicController {

    private final ServiceOfferingService serviceOfferingService;

    @GetMapping
    @Operation(summary = "Get Active Service Offerings", description = "Returns active architectural and engineering services")
    public ResponseEntity<ApiResponse<List<ServiceDto>>> getServices() {
        List<ServiceDto> list = serviceOfferingService.getActiveServices();
        return ResponseEntity.ok(ApiResponse.ok(list));
    }
}
