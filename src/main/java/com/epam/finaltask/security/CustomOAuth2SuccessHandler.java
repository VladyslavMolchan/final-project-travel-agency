package com.epam.finaltask.security;

import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        log.info("OAuth login attempt for email: {}", email);

        User user = userRepository.findUserByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setUsername(name);
            newUser.setEmail(email);
            newUser.setPassword(""); // пароль не потрібен
            newUser.setRole(Role.USER);
            newUser.setActive(true);
            newUser.setBalance(BigDecimal.ZERO);
            log.info("Registering new user via OAuth: {}", email);
            return userRepository.save(newUser);
        });

        if (!user.getPassword().isEmpty()) {
            log.warn("OAuth login blocked. Email already registered with password: {}", email);
            response.sendRedirect("/auth/sign-in?error=Email вже зареєстрований. Використайте логін.");
            return;
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        Cookie accessCookie = new Cookie("jwt", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge((int) (jwtUtil.getAccessTokenExpirationMs() / 1000));
        accessCookie.setAttribute("SameSite", "Lax");
        response.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) (jwtUtil.getRefreshTokenExpirationMs() / 1000));
        refreshCookie.setAttribute("SameSite", "Lax");
        response.addCookie(refreshCookie);

        log.info("OAuth login successful for '{}'", user.getEmail());
        response.sendRedirect("/");
    }
}
