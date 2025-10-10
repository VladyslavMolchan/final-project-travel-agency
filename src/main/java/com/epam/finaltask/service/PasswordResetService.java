package com.epam.finaltask.service;

import com.epam.finaltask.model.PasswordResetToken;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.PasswordResetTokenRepository;
import com.epam.finaltask.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public void createPasswordResetToken(String email, String token) {
        userRepository.findUserByEmail(email).ifPresentOrElse(user -> {
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .token(token)
                    .user(user)
                    .expiryDate(LocalDateTime.now().plusHours(1))
                    .build();
            tokenRepository.save(resetToken);
            log.info("Password reset token created for user with email {}", email);
        }, () -> {
            log.warn("Attempted to create password reset token for non-existent email: {}", email);
        });
    }

    public Optional<User> validatePasswordResetToken(String token) {
        Optional<User> user = tokenRepository.findByToken(token)
                .filter(t -> !t.isExpired())
                .map(PasswordResetToken::getUser);

        if (user.isPresent()) {
            log.info("Password reset token {} validated successfully", token);
        } else {
            log.warn("Invalid or expired password reset token: {}", token);
        }
        return user;
    }
}
