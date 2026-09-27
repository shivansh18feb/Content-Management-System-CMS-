package com.portfolio.cms.media.repository;

import com.portfolio.cms.media.entity.Media;
import com.portfolio.cms.media.entity.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

    Optional<Media> findByStoredFilename(String storedFilename);

    @Query(value = "SELECT * FROM media m WHERE " +
           "(CAST(:mediaType AS varchar) IS NULL OR m.media_type = CAST(:mediaType AS varchar)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(m.original_filename) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')) OR " +
           " LOWER(COALESCE(m.alt_text, '')) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))",
           countQuery = "SELECT count(*) FROM media m WHERE " +
           "(CAST(:mediaType AS varchar) IS NULL OR m.media_type = CAST(:mediaType AS varchar)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(m.original_filename) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')) OR " +
           " LOWER(COALESCE(m.alt_text, '')) LIKE LOWER(CONCAT('%', CAST(:search AS varchar), '%')))",
           nativeQuery = true)
    Page<Media> findWithFilters(@Param("search") String search,
                                @Param("mediaType") String mediaType,
                                Pageable pageable);
}
