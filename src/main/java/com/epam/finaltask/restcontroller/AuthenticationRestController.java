package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.LoginRequestDto;
import com.epam.finaltask.dto.ResetPasswordDto;
import com.epam.finaltask.dto.UserRegistrationDto;
import com.epam.finaltask.exception.*;
import com.epam.finaltask.model.RefreshToken;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.security.JwtUtil;
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
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

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
            throw new UserAlreadyExistsException();
        }

        return ResponseEntity.ok(Map.of("message", "Registration successful"));
    }

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest, HttpServletResponse response) {
        boolean authenticated = authService.login(loginRequest);
        if (!authenticated) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtUtil.generateToken(loginRequest.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.getUsername());

        Cookie accessCookie = new Cookie("jwt", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken.getToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(86400 * 7);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "accessToken", accessToken,
                "refreshToken", refreshToken.getToken()
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
                    String newAccessToken = jwtUtil.generateToken(user.getUsername());

                    Cookie accessCookie = new Cookie("jwt", newAccessToken);
                    accessCookie.setHttpOnly(true);
                    accessCookie.setPath("/");
                    accessCookie.setMaxAge((int) (jwtUtil.getExpirationMs() / 1000));
                    response.addCookie(accessCookie);

                    return ResponseEntity.ok(Map.of(
                            "accessToken", newAccessToken,
                            "refreshToken", refreshTokenStr
                    ));
                })
                .orElseThrow(InvalidRefreshTokenException::new);
    }

}
