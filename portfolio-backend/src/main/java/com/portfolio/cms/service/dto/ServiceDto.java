package com.portfolio.cms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDto {
    private Long id;
    private String title;
    private String description;
    private String icon;
    private List<String> features;
    private int displayOrder;
    private boolean active;
}
