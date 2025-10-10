package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import com.epam.finaltask.model.PasswordResetToken;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.PasswordResetTokenRepository;
import com.epam.finaltask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createPasswordResetToken_UserExists_SavesToken() {
        String email = "test@example.com";
        String token = "token123";

        User user = new User();
        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));

        passwordResetService.createPasswordResetToken(email, token);


        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(captor.capture());

        PasswordResetToken savedToken = captor.getValue();
        assertEquals(token, savedToken.getToken());
        assertEquals(user, savedToken.getUser());
        assertTrue(savedToken.getExpiryDate().isAfter(LocalDateTime.now()));

        verify(userRepository).findUserByEmail(email);
    }

    @Test
    void createPasswordResetToken_UserNotFound_LogsWarning() {
        String email = "notfound@example.com";
        String token = "token123";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        passwordResetService.createPasswordResetToken(email, token);

        verify(userRepository).findUserByEmail(email);
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void validatePasswordResetToken_ValidToken_ReturnsUser() {
        String token = "validToken";

        User user = new User();
        PasswordResetToken resetToken = mock(PasswordResetToken.class);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(resetToken.isExpired()).thenReturn(false);
        when(resetToken.getUser()).thenReturn(user);

        Optional<User> result = passwordResetService.validatePasswordResetToken(token);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());

        verify(tokenRepository).findByToken(token);
        verify(resetToken).isExpired();
        verify(resetToken).getUser();
    }

    @Test
    void validatePasswordResetToken_ExpiredToken_ReturnsEmpty() {
        String token = "expiredToken";

        PasswordResetToken resetToken = mock(PasswordResetToken.class);

        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));
        when(resetToken.isExpired()).thenReturn(true);

        Optional<User> result = passwordResetService.validatePasswordResetToken(token);

        assertTrue(result.isEmpty());

        verify(tokenRepository).findByToken(token);
        verify(resetToken).isExpired();
        verify(resetToken, never()).getUser();
    }

    @Test
    void validatePasswordResetToken_TokenNotFound_ReturnsEmpty() {
        String token = "missingToken";

        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        Optional<User> result = passwordResetService.validatePasswordResetToken(token);

        assertTrue(result.isEmpty());

        verify(tokenRepository).findByToken(token);
    }
}
