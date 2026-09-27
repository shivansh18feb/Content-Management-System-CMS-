package com.portfolio.cms.blog.repository;

import com.portfolio.cms.blog.entity.Blog;
import com.portfolio.cms.blog.entity.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    Optional<Blog> findBySlugAndStatus(String slug, BlogStatus status);

    Optional<Blog> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    @Query(value = "SELECT b.* FROM blogs b " +
           "LEFT JOIN blog_post_tags bpt ON bpt.blog_id = b.id " +
           "LEFT JOIN blog_tags t ON t.id = bpt.tag_id " +
           "LEFT JOIN blog_categories bc ON bc.id = b.category_id " +
           "WHERE b.status = 'PUBLISHED' AND " +
           "(CAST(:categorySlug AS varchar) IS NULL OR bc.slug = CAST(:categorySlug AS varchar)) AND " +
           "(CAST(:tagSlug AS varchar) IS NULL OR t.slug = CAST(:tagSlug AS varchar)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(b.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(b.excerpt) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           countQuery = "SELECT COUNT(DISTINCT b.id) FROM blogs b " +
           "LEFT JOIN blog_post_tags bpt ON bpt.blog_id = b.id " +
           "LEFT JOIN blog_tags t ON t.id = bpt.tag_id " +
           "LEFT JOIN blog_categories bc ON bc.id = b.category_id " +
           "WHERE b.status = 'PUBLISHED' AND " +
           "(CAST(:categorySlug AS varchar) IS NULL OR bc.slug = CAST(:categorySlug AS varchar)) AND " +
           "(CAST(:tagSlug AS varchar) IS NULL OR t.slug = CAST(:tagSlug AS varchar)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(b.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(b.excerpt) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           nativeQuery = true)
    Page<Blog> findPublishedWithFilters(@Param("search") String search,
                                        @Param("categorySlug") String categorySlug,
                                        @Param("tagSlug") String tagSlug,
                                        Pageable pageable);

    @Query(value = "SELECT * FROM blogs b WHERE " +
           "(CAST(:status AS varchar) IS NULL OR b.status = CAST(:status AS varchar)) AND " +
           "(CAST(:categoryId AS bigint) IS NULL OR b.category_id = CAST(:categoryId AS bigint)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(b.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(b.excerpt) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           countQuery = "SELECT COUNT(*) FROM blogs b WHERE " +
           "(CAST(:status AS varchar) IS NULL OR b.status = CAST(:status AS varchar)) AND " +
           "(CAST(:categoryId AS bigint) IS NULL OR b.category_id = CAST(:categoryId AS bigint)) AND " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(b.title) LIKE LOWER('%' || CAST(:search AS varchar) || '%') OR " +
           " LOWER(b.excerpt) LIKE LOWER('%' || CAST(:search AS varchar) || '%'))",
           nativeQuery = true)
    Page<Blog> findAdminWithFilters(@Param("search") String search,
                                    @Param("categoryId") Long categoryId,
                                    @Param("status") String status,
                                    Pageable pageable);

    long countByStatus(BlogStatus status);
}
