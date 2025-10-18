package com.epam.finaltask.service;

import com.epam.finaltask.dto.LoginRequestDto;
import com.epam.finaltask.dto.UserRegistrationDto;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       LoginAttemptService loginAttemptService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
        this.refreshTokenService = refreshTokenService;
    }


    public boolean register(UserRegistrationDto registrationDto, Role role) {
        log.info("Attempting to register user: {}", registrationDto.getUsername());

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            log.warn("Registration failed: username already exists: {}", registrationDto.getUsername());
            return false;
        }
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            log.warn("Registration failed: email already exists: {}", registrationDto.getEmail());
            return false;
        }

        User newUser = new User();
        newUser.setUsername(registrationDto.getUsername());
        newUser.setEmail(registrationDto.getEmail());
        newUser.setPhoneNumber(registrationDto.getPhoneNumber());
        newUser.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        newUser.setRole(role);
        newUser.setActive(true);
        newUser.setBalance(BigDecimal.ZERO);

        userRepository.save(newUser);
        log.info("User registered successfully: {}", registrationDto.getUsername());
        return true;
    }


    public boolean login(LoginRequestDto loginRequest) {
        String usernameOrEmail = loginRequest.getUsername().trim();
        log.info("Login attempt for user: {}", usernameOrEmail);

        // 🔐 Перевірка блокування
        if (loginAttemptService.isBlocked(usernameOrEmail)) {
            log.warn("Login blocked for user: {}", usernameOrEmail);
            throw new IllegalStateException("Account temporarily locked. Try again later.");
        }


        Optional<User> userOpt = userRepository.findUserByUsername(usernameOrEmail)
                .or(() -> userRepository.findUserByEmail(usernameOrEmail));

        if (userOpt.isEmpty()) {
            log.warn("Login failed: user not found: {}", usernameOrEmail);
            loginAttemptService.loginFailed(usernameOrEmail);
            return false;
        }

        User user = userOpt.get();


        if (!user.isActive()) {
            log.warn("Login failed: user '{}' is inactive", usernameOrEmail);
            return false;
        }

        boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        log.debug("Password match result for '{}': {}", usernameOrEmail, passwordMatches);

        if (passwordMatches) {
            loginAttemptService.loginSucceeded(usernameOrEmail);
            log.info("Login successful for user: {}", usernameOrEmail);
            return true;
        } else {
            loginAttemptService.loginFailed(usernameOrEmail);
            log.warn("Login failed: incorrect password for user: {}", usernameOrEmail);
            return false;
        }
    }


    public void updatePassword(User user, String newPassword) {
        log.info("Updating password for user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    public Map<String, String> generateTokens(String username) {
        log.debug("Generating tokens for user: {}", username);
        String accessToken = jwtUtil.generateAccessToken(username); // або generateToken()
        String refreshToken = refreshTokenService.createRefreshToken(username).getToken();

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
