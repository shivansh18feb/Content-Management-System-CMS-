package com.portfolio.cms.service;

import com.portfolio.cms.audit.service.AuditLogService;
import com.portfolio.cms.auth.dto.AuthResponse;
import com.portfolio.cms.auth.dto.ChangePasswordRequest;
import com.portfolio.cms.auth.dto.LoginRequest;
import com.portfolio.cms.auth.entity.RefreshToken;
import com.portfolio.cms.auth.repository.RefreshTokenRepository;
import com.portfolio.cms.auth.service.AuthServiceImpl;
import com.portfolio.cms.common.exception.BadRequestException;
import com.portfolio.cms.security.JwtTokenProvider;
import com.portfolio.cms.security.UserPrincipal;
import com.portfolio.cms.user.entity.Role;
import com.portfolio.cms.user.entity.User;
import com.portfolio.cms.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("admin@portfolio.com")
                .fullName("Admin User")
                .passwordHash("$2a$12$hashedPassword")
                .role(Role.ADMIN)
                .enabled(true)
                .accountLocked(false)
                .build();
        testUser.setId(1L);

        principal = UserPrincipal.create(testUser);
    }

    @Test
    @DisplayName("Should successfully login and return JWT + Refresh Token")
    void shouldLoginSuccessfully() {
        LoginRequest request = new LoginRequest("admin@portfolio.com", "Admin@123456");
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateAccessToken(authentication)).thenReturn("jwt.token.string");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthResponse response = authService.login(request, "127.0.0.1");

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("jwt.token.string");
        assertThat(response.getUser().getEmail()).isEqualTo("admin@portfolio.com");
        assertThat(response.getUser().getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).save(testUser);
        verify(auditLogService).log(eq(1L), eq("admin@portfolio.com"), eq("LOGIN"), eq("User"), eq("1"), eq("127.0.0.1"), any());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid credentials")
    void shouldFailLoginOnBadCredentials() {
        LoginRequest request = new LoginRequest("admin@portfolio.com", "WrongPassword");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request, "127.0.0.1"))
                .isInstanceOf(BadCredentialsException.class);

        verify(tokenProvider, never()).generateAccessToken(any(Authentication.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if current password does not match when changing password")
    void shouldFailChangePasswordOnCurrentMismatch() {
        ChangePasswordRequest request = new ChangePasswordRequest("WrongCurrent", "NewPassword123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("WrongCurrent", testUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.changePassword(1L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Current password is incorrect");

        verify(userRepository, never()).save(any());
    }
}
