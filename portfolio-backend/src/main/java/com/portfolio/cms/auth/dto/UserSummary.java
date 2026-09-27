package com.portfolio.cms.auth.dto;

import com.portfolio.cms.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String email;
    private String fullName;
    private Role role;
}
