package com.portfolio.cms.config;

import com.portfolio.cms.user.entity.Role;
import com.portfolio.cms.user.entity.User;
import com.portfolio.cms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "admin@portfolio.com";
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                user -> {
                    // Ensure the seeded password strictly matches our standard BCrypt hash
                    user.setPasswordHash(passwordEncoder.encode("Admin@123456"));
                    user.setEnabled(true);
                    user.setAccountLocked(false);
                    userRepository.save(user);
                    log.info("System administrator account verified for: {}", adminEmail);
                },
                () -> {
                    User admin = User.builder()
                            .email(adminEmail)
                            .passwordHash(passwordEncoder.encode("Admin@123456"))
                            .fullName("System Administrator")
                            .role(Role.ADMIN)
                            .enabled(true)
                            .accountLocked(false)
                            .build();
                    userRepository.save(admin);
                    log.info("Default system administrator account created: {}", adminEmail);
                }
        );
    }
}
