package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.LoginRequestDto;
import com.epam.finaltask.dto.ResetPasswordDto;
import com.epam.finaltask.dto.UserRegistrationDto;
import com.epam.finaltask.exception.*;
import com.epam.finaltask.model.RefreshToken;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.service.AuthService;
import com.epam.finaltask.service.PasswordResetService;
import com.epam.finaltask.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationRestController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final RefreshTokenService refreshTokenService;

    // --- Registration ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("Registration attempt for user: {}", registrationDto.getUsername());

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            log.warn("Password mismatch for user: {}", registrationDto.getUsername());
            throw new PasswordsDoNotMatchException();
        }

        Role role;
        try {
            role = Role.valueOf(registrationDto.getRole().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Invalid role provided: {}", registrationDto.getRole());
            throw new InvalidRoleException();
        }

        boolean success = authService.register(registrationDto, role);
        if (!success) {
            log.warn("Registration failed for user '{}': already exists", registrationDto.getUsername());
            throw new UserAlreadyExistsException("Email або Username вже існує!");
        }

        log.info("User '{}' successfully registered with role '{}'", registrationDto.getUsername(), role);

        Map<String, String> tokens = authService.generateTokens(registrationDto.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "Registration successful",
                "accessToken", tokens.get("accessToken"),
                "refreshToken", tokens.get("refreshToken")
        ));
    }

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        log.info("Login attempt for user: {}", loginRequest.getUsername());

        boolean authenticated = authService.login(loginRequest);
        if (!authenticated) {
            log.warn("Login failed for user: {}", loginRequest.getUsername());
            throw new InvalidCredentialsException();
        }

        log.info("User '{}' successfully logged in", loginRequest.getUsername());

        Map<String, String> tokens = authService.generateTokens(loginRequest.getUsername());

        Cookie accessCookie = new Cookie("jwt", tokens.get("accessToken"));
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(86400 * 7);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "accessToken", tokens.get("accessToken"),
                "refreshToken", tokens.get("refreshToken")
        ));
    }

    // --- Reset Password ---
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token,
                                           @Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        log.info("Password reset attempt with token: {}", token);

        Optional<User> userOpt = passwordResetService.validatePasswordResetToken(token);
        if (userOpt.isEmpty()) {
            log.warn("Invalid or expired password reset token: {}", token);
            throw new InvalidOrExpiredTokenException("Invalid or expired token");
        }

        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())) {
            log.warn("Password mismatch during reset for user: {}", userOpt.get().getUsername());
            throw new PasswordsDoNotMatchException();
        }

        authService.updatePassword(userOpt.get(), resetPasswordDto.getPassword());

        log.info("Password successfully reset for user: {}", userOpt.get().getUsername());

        return ResponseEntity.ok(Map.of("message", "Password successfully reset"));
    }

    // --- Refresh Token ---
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String refreshTokenStr = request.get("refreshToken");
        log.info("Refresh token attempt");

        return refreshTokenService.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    log.info("Tokens refreshed for user: {}", user.getUsername());

                    Map<String, String> tokens = authService.generateTokens(user.getUsername());

                    Cookie accessCookie = new Cookie("jwt", tokens.get("accessToken"));
                    accessCookie.setHttpOnly(true);
                    accessCookie.setPath("/");
                    accessCookie.setMaxAge(3600);
                    response.addCookie(accessCookie);

                    Cookie refreshCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
                    refreshCookie.setHttpOnly(true);
                    refreshCookie.setPath("/");
                    refreshCookie.setMaxAge(86400 * 7);
                    response.addCookie(refreshCookie);

                    return ResponseEntity.ok(Map.of(
                            "accessToken", tokens.get("accessToken"),
                            "refreshToken", tokens.get("refreshToken")
                    ));
                })
                .orElseThrow(() -> {
                    log.warn("Invalid refresh token used: {}", refreshTokenStr);
                    return new InvalidRefreshTokenException();
                });
    }
}
