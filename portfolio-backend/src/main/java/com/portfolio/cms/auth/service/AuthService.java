package com.portfolio.cms.auth.service;

import com.portfolio.cms.auth.dto.AuthResponse;
import com.portfolio.cms.auth.dto.ChangePasswordRequest;
import com.portfolio.cms.auth.dto.LoginRequest;
import com.portfolio.cms.auth.dto.TokenRefreshRequest;
import com.portfolio.cms.auth.dto.UserSummary;

public interface AuthService {
    AuthResponse login(LoginRequest request, String clientIp);
    AuthResponse refreshToken(TokenRefreshRequest request, String clientIp);
    void logout(Long userId, String clientIp);
    void changePassword(Long userId, ChangePasswordRequest request);
    UserSummary getCurrentUser(Long userId);
}
