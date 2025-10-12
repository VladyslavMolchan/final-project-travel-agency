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
import java.util.Optional;



@Service
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.loginAttemptService = loginAttemptService;
    }

    public boolean register(UserRegistrationDto registrationDto, Role role) {

        log.info("Attempting to register user: {}", registrationDto.getUsername());

        // Перевірка унікальності
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
        String username = loginRequest.getUsername();
        log.info("Login attempt for user: {}", username);


        if (loginAttemptService.isBlocked(username)) {
            log.warn("Login blocked for user: {}", username);
            throw new IllegalStateException("Account temporarily locked. Try again later.");
        }

        Optional<User> userOpt = userRepository.findUserByUsername(username);
        if (userOpt.isEmpty()) {
            log.warn("Login failed: user not found: {}", username);
            loginAttemptService.loginFailed(username);
            return false;
        }

        User user = userOpt.get();

        boolean success = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if (success) {
            loginAttemptService.loginSucceeded(username);
            log.info("Login successful for user: {}", username);
        } else {
            loginAttemptService.loginFailed(username);
            log.warn("Login failed: incorrect password for user: {}", username);
        }

        return success;
    }

    public void updatePassword(User user, String newPassword) {
        log.info("Updating password for user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public String generateToken(String username) {
        log.debug("Generating token for user: {}", username);
        return jwtUtil.generateToken(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

}
