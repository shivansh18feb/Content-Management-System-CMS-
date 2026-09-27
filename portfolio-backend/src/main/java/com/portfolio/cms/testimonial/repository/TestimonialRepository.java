package com.portfolio.cms.testimonial.repository;

import com.portfolio.cms.testimonial.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {
    List<Testimonial> findByPublishedTrueOrderByDisplayOrderAsc();
    List<Testimonial> findByPublishedTrueAndFeaturedTrueOrderByDisplayOrderAsc();
    List<Testimonial> findAllByOrderByDisplayOrderAsc();
    long countByPublishedTrue();
}
