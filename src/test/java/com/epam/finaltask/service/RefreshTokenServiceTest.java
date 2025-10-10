package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.epam.finaltask.model.RefreshToken;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.RefreshTokenRepository;
import com.epam.finaltask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        refreshTokenService.refreshTokenDurationMs = 3600000L;
    }

    @Test
    void createRefreshToken_UserExists_CreatesAndSavesToken() {
        String username = "testUser";
        User user = new User();
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        doNothing().when(refreshTokenRepository).deleteByUser(user);

        RefreshToken savedToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenService.refreshTokenDurationMs))
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedToken);

        RefreshToken result = refreshTokenService.createRefreshToken(username);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
        assertTrue(result.getExpiryDate().isAfter(Instant.now()));

        verify(userRepository).findUserByUsername(username);
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void createRefreshToken_UserNotFound_ThrowsException() {
        String username = "nonExistentUser";

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.createRefreshToken(username);
        });

        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findUserByUsername(username);
        verify(refreshTokenRepository, never()).deleteByUser(any());
        verify(refreshTokenRepository, never()).save(any());
    }

    @Test
    void findByToken_TokenExists_ReturnsOptional() {
        String tokenString = "token123";
        RefreshToken token = new RefreshToken();
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.of(token));

        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        assertTrue(result.isPresent());
        assertEquals(token, result.get());
        verify(refreshTokenRepository).findByToken(tokenString);
    }

    @Test
    void findByToken_TokenNotFound_ReturnsEmpty() {
        String tokenString = "missingToken";
        when(refreshTokenRepository.findByToken(tokenString)).thenReturn(Optional.empty());

        Optional<RefreshToken> result = refreshTokenService.findByToken(tokenString);

        assertTrue(result.isEmpty());
        verify(refreshTokenRepository).findByToken(tokenString);
    }

    @Test
    void verifyExpiration_TokenNotExpired_ReturnsToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));

        RefreshToken result = refreshTokenService.verifyExpiration(token);

        assertEquals(token, result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_TokenExpired_DeletesAndThrows() {
        RefreshToken token = mock(RefreshToken.class);
        when(token.getExpiryDate()).thenReturn(Instant.now().minusSeconds(1));

        doNothing().when(refreshTokenRepository).delete(token);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            refreshTokenService.verifyExpiration(token);
        });

        assertEquals("Refresh token expired. Please login again.", exception.getMessage());

        verify(token).getExpiryDate();
        verify(refreshTokenRepository).delete(token);
    }
}
