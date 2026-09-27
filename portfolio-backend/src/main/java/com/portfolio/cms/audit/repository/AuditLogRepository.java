package com.portfolio.cms.audit.repository;

import com.portfolio.cms.audit.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query(value = "SELECT * FROM audit_logs a WHERE " +
           "(CAST(:action AS varchar) IS NULL OR LOWER(a.action) = LOWER(CAST(:action AS varchar))) AND " +
           "(CAST(:entityType AS varchar) IS NULL OR LOWER(a.entity_type) = LOWER(CAST(:entityType AS varchar)))",
           countQuery = "SELECT COUNT(*) FROM audit_logs a WHERE " +
           "(CAST(:action AS varchar) IS NULL OR LOWER(a.action) = LOWER(CAST(:action AS varchar))) AND " +
           "(CAST(:entityType AS varchar) IS NULL OR LOWER(a.entity_type) = LOWER(CAST(:entityType AS varchar)))",
           nativeQuery = true)
    Page<AuditLog> findWithFilters(@Param("action") String action,
                                   @Param("entityType") String entityType,
                                   Pageable pageable);
}
