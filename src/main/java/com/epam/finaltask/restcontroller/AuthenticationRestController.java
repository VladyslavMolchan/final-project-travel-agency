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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationRestController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;
    private final RefreshTokenService refreshTokenService;

    // --- Registration ---
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        Role role;
        try {
            role = Role.valueOf(registrationDto.getRole().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidRoleException();
        }

        boolean success = authService.register(registrationDto, role);
        if (!success) {
            throw new UserAlreadyExistsException("Email або Username вже існує!");
        }


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
        boolean authenticated = authService.login(loginRequest);
        if (!authenticated) {
            throw new InvalidCredentialsException();
        }

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

        Optional<User> userOpt = passwordResetService.validatePasswordResetToken(token);
        if (userOpt.isEmpty()) {
            throw new InvalidOrExpiredTokenException("Invalid or expired token");
        }

        if (!resetPasswordDto.getPassword().equals(resetPasswordDto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        authService.updatePassword(userOpt.get(), resetPasswordDto.getPassword());

        return ResponseEntity.ok(Map.of("message", "Password successfully reset"));
    }

    // --- Refresh Token ---
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String refreshTokenStr = request.get("refreshToken");

        return refreshTokenService.findByToken(refreshTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
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
                .orElseThrow(InvalidRefreshTokenException::new);
    }
}
