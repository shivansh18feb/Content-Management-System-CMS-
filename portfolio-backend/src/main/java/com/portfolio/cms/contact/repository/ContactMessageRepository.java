package com.portfolio.cms.contact.repository;

import com.portfolio.cms.contact.entity.ContactMessage;
import com.portfolio.cms.contact.entity.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    @Query(value = "SELECT * FROM messages m WHERE " +
           "(CAST(:status AS varchar) IS NULL OR m.status = CAST(:status AS varchar)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(m.name) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(m.email) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(m.subject) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           countQuery = "SELECT COUNT(*) FROM messages m WHERE " +
           "(CAST(:status AS varchar) IS NULL OR m.status = CAST(:status AS varchar)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(m.name) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(m.email) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(m.subject) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           nativeQuery = true)
    Page<ContactMessage> findWithFilters(@Param("search") String search,
                                         @Param("status") String status,
                                         Pageable pageable);

    long countByStatus(MessageStatus status);
}
