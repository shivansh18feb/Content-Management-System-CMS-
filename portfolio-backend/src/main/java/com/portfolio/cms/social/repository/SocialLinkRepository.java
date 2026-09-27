package com.portfolio.cms.social.repository;

import com.portfolio.cms.social.entity.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SocialLinkRepository extends JpaRepository<SocialLink, Long> {
    List<SocialLink> findByActiveTrueOrderByDisplayOrderAsc();
    List<SocialLink> findAllByOrderByDisplayOrderAsc();
}
