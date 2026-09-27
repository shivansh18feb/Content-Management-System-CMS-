package com.portfolio.cms.skill.repository;

import com.portfolio.cms.skill.entity.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    List<Skill> findByActiveTrueOrderByDisplayOrderAsc();

    List<Skill> findByActiveTrueAndFeaturedTrueOrderByDisplayOrderAsc();

    List<Skill> findByActiveTrueAndCategoryIdOrderByDisplayOrderAsc(Long categoryId);

    @Query(value = "SELECT * FROM skills s WHERE " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(s.name) LIKE LOWER('%' || CAST(:search AS varchar) || '%')) AND " +
           "(CAST(:categoryId AS bigint) IS NULL OR s.category_id = CAST(:categoryId AS bigint)) AND " +
           "(CAST(:active AS boolean) IS NULL OR s.active = CAST(:active AS boolean))",
           countQuery = "SELECT COUNT(*) FROM skills s WHERE " +
           "(CAST(:search AS varchar) IS NULL OR LOWER(s.name) LIKE LOWER('%' || CAST(:search AS varchar) || '%')) AND " +
           "(CAST(:categoryId AS bigint) IS NULL OR s.category_id = CAST(:categoryId AS bigint)) AND " +
           "(CAST(:active AS boolean) IS NULL OR s.active = CAST(:active AS boolean))",
           nativeQuery = true)
    Page<Skill> findWithFilters(@Param("search") String search,
                                @Param("categoryId") Long categoryId,
                                @Param("active") Boolean active,
                                Pageable pageable);
}
