package com.portfolio.cms.audit.controller;

import com.portfolio.cms.audit.entity.AuditLog;
import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.common.response.ApiResponse;
import com.portfolio.cms.common.response.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Admin-only audit log viewer")
public class AuditLogAdminController {

    private final AuditLogService auditLogService;

    @GetMapping
    @Operation(summary = "List audit logs", description = "Paginated audit log entries, filterable by action and entity type. ADMIN only.")
    public ResponseEntity<ApiResponse<PagedResponse<AuditLog>>> getLogs(
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<AuditLog> logs = auditLogService.getLogs(action, entityType, pageable);
        return ResponseEntity.ok(ApiResponse.ok(logs));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log entry", description = "Retrieve a single audit log entry by ID. ADMIN only.")
    public ResponseEntity<ApiResponse<AuditLog>> getById(@PathVariable Long id) {
        AuditLog log = auditLogService.findById(id);
        return ResponseEntity.ok(ApiResponse.ok(log));
    }
}
