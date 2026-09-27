package com.portfolio.cms.skill.repository;

import com.portfolio.cms.skill.entity.SkillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {
    List<SkillCategory> findAllByOrderByDisplayOrderAsc();
    Optional<SkillCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
