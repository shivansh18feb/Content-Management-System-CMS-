package com.portfolio.cms.audit.service;

import com.portfolio.cms.audit.entity.AuditLog;
import com.portfolio.cms.common.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void log(Long userId, String userEmail, String action, String entityType, String entityId, String ipAddress, String metadata);
    PagedResponse<AuditLog> getLogs(String action, String entityType, Pageable pageable);
    AuditLog findById(Long id);
}
