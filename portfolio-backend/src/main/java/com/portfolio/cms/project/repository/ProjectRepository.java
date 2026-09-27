package com.portfolio.cms.project.repository;

import com.portfolio.cms.project.entity.Project;
import com.portfolio.cms.project.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findBySlugAndStatus(String slug, ProjectStatus status);

    Optional<Project> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    List<Project> findByStatusAndFeaturedTrueOrderByDisplayOrderAsc(ProjectStatus status);

    @Query(value = "SELECT * FROM projects p WHERE p.status = 'PUBLISHED' AND " +
           "(CAST(:category AS varchar) IS NULL OR LOWER(p.category) = LOWER(CAST(:category AS varchar))) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(p.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(p.short_description) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           countQuery = "SELECT COUNT(*) FROM projects p WHERE p.status = 'PUBLISHED' AND " +
           "(CAST(:category AS varchar) IS NULL OR LOWER(p.category) = LOWER(CAST(:category AS varchar))) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(p.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(p.short_description) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           nativeQuery = true)
    Page<Project> findPublishedWithFilters(@Param("search") String search,
                                          @Param("category") String category,
                                          Pageable pageable);

    @Query(value = "SELECT * FROM projects p WHERE " +
           "(CAST(:status AS varchar) IS NULL OR p.status = CAST(:status AS varchar)) AND " +
           "(CAST(:category AS varchar) IS NULL OR LOWER(p.category) = LOWER(CAST(:category AS varchar))) AND " +
           "(CAST(:featured AS boolean) IS NULL OR p.featured = CAST(:featured AS boolean)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(p.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(p.short_description) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           countQuery = "SELECT COUNT(*) FROM projects p WHERE " +
           "(CAST(:status AS varchar) IS NULL OR p.status = CAST(:status AS varchar)) AND " +
           "(CAST(:category AS varchar) IS NULL OR LOWER(p.category) = LOWER(CAST(:category AS varchar))) AND " +
           "(CAST(:featured AS boolean) IS NULL OR p.featured = CAST(:featured AS boolean)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(p.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(p.short_description) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           nativeQuery = true)
    Page<Project> findAdminWithFilters(@Param("search") String search,
                                       @Param("category") String category,
                                       @Param("status") String status,
                                       @Param("featured") Boolean featured,
                                       Pageable pageable);

    long countByStatus(ProjectStatus status);
}
