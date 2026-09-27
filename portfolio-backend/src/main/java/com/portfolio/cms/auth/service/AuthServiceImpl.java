package com.portfolio.cms.auth.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.auth.dto.*;
import com.portfolio.cms.auth.entity.RefreshToken;
import com.portfolio.cms.auth.repository.RefreshTokenRepository;
import com.portfolio.cms.common.exception.BadRequestException;
import com.portfolio.cms.common.exception.ResourceNotFoundException;
import com.portfolio.cms.common.exception.UnauthorizedException;
import com.portfolio.cms.security.JwtTokenProvider;
import com.portfolio.cms.security.UserPrincipal;
import com.portfolio.cms.user.entity.User;
import com.portfolio.cms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String clientIp) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        RefreshToken refreshToken = createRefreshToken(user);

        auditLogService.log(user.getId(), user.getEmail(), "LOGIN", "User", String.valueOf(user.getId()), clientIp, "Successful login");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(900) // 15 mins
                .user(mapToUserSummary(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request, String clientIp) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (existingToken.isRevoked() || existingToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token is expired or revoked. Please log in again.");
        }

        User user = existingToken.getUser();
        if (!user.isEnabled() || user.isAccountLocked()) {
            throw new UnauthorizedException("User account is disabled or locked");
        }

        // Token Rotation: revoke existing, generate fresh one
        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        RefreshToken newRefreshToken = createRefreshToken(user);
        String newAccessToken = tokenProvider.generateAccessToken(
                user.getEmail(),
                user.getId(),
                "ROLE_" + user.getRole().name(),
                user.getFullName()
        );

        auditLogService.log(user.getId(), user.getEmail(), "REFRESH_TOKEN", "RefreshToken", String.valueOf(newRefreshToken.getId()), clientIp, "Token rotated successfully");

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(900)
                .user(mapToUserSummary(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(Long userId, String clientIp) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenRepository.revokeAllUserTokens(user);
        auditLogService.log(user.getId(), user.getEmail(), "LOGOUT", "User", String.valueOf(user.getId()), clientIp, "User logged out");
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password cannot be the same as your current password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate active sessions to enforce re-authentication
        refreshTokenRepository.revokeAllUserTokens(user);
        auditLogService.log(user.getId(), user.getEmail(), "CHANGE_PASSWORD", "User", String.valueOf(user.getId()), null, "Password changed successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public UserSummary getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserSummary(user);
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", ""))
                .expiryDate(Instant.now().plusMillis(tokenProvider.getRefreshExpirationMs()))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    private UserSummary mapToUserSummary(User user) {
        return UserSummary.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
