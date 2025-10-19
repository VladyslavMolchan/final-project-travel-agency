package com.epam.finaltask.service;

import com.epam.finaltask.exception.UserNotFoundException;
import com.epam.finaltask.model.RefreshToken;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.RefreshTokenRepository;
import com.epam.finaltask.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Value("${application.security.jwt.refresh-token.expiration}")
    Long refreshTokenDurationMs;

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        log.info("Creating refresh token for user: {}", username);
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UserNotFoundException("User not found");
                });

        log.debug("Deleting existing refresh tokens for user: {}", username);
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .build();

        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for user {}: {}", username, savedToken.getToken());

        return savedToken;
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        log.debug("Verifying expiration for refresh token: {}", token.getToken());
        if (token.getExpiryDate().isBefore(Instant.now())) {
            log.warn("Refresh token expired: {}", token.getToken());
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }

    public Optional<RefreshToken> findByToken(String token) {
        log.debug("Finding refresh token: {}", token);
        return refreshTokenRepository.findByToken(token);
    }
}
