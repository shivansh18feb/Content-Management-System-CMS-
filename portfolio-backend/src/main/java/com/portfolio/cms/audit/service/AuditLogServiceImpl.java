package com.portfolio.cms.audit.service;

import com.portfolio.cms.audit.entity.AuditLog;
import com.portfolio.cms.audit.repository.AuditLogRepository;
import com.portfolio.cms.common.response.PagedResponse;
import com.portfolio.cms.common.util.PageableUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void log(Long userId, String userEmail, String action, String entityType, String entityId, String ipAddress, String metadata) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .userEmail(userEmail)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .ipAddress(ipAddress)
                    .metadata(metadata)
                    .build();
            auditLogRepository.save(auditLog);
        } catch (Exception ex) {
            log.error("Failed to persist audit log: {}", ex.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AuditLog> getLogs(String action, String entityType, Pageable pageable) {
        Pageable snakePageable = PageableUtils.toSnakeCase(pageable, "created_at", Sort.Direction.DESC);
        Page<AuditLog> logs = auditLogRepository.findWithFilters(action, entityType, snakePageable);
        return PagedResponse.of(logs);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLog findById(Long id) {
        return auditLogRepository.findById(id)
                .orElseThrow(() -> new com.portfolio.cms.common.exception.ResourceNotFoundException("AuditLog", "id", id));
    }
}
