package com.portfolio.cms.service.repository;

import com.portfolio.cms.service.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    List<ServiceOffering> findByActiveTrueOrderByDisplayOrderAsc();
    List<ServiceOffering> findAllByOrderByDisplayOrderAsc();
}
