package com.epam.finaltask.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCK_TIME_MS = 5 * 60 * 1000;

    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, Long> lockTime = new ConcurrentHashMap<>();


    private Clock clock;


    public LoginAttemptService() {
        this(Clock.systemDefaultZone());
    }

    public LoginAttemptService(Clock clock) {
        this.clock = clock;
    }


    public void loginSucceeded(String username) {
        log.info("Login succeeded for user: {}", username);
        attempts.remove(username);
        lockTime.remove(username);
    }

    public void loginFailed(String username) {
        if (isBlocked(username)) {
            log.warn("Login failed for user: {} - user is currently locked", username);
            return;
        }

        int currentAttempts = attempts.merge(username, 1, Integer::sum);
        log.warn("Login failed for user: {} (attempt {}/{})", username, currentAttempts, MAX_ATTEMPTS);

        if (currentAttempts >= MAX_ATTEMPTS) {
            lockTime.put(username, clock.millis());
            log.warn("User '{}' is temporarily locked due to too many failed login attempts", username);
        }
    }


    public boolean isBlocked(String username) {
        Long lockTimestamp = lockTime.get(username);
        if (lockTimestamp == null) {
            return false;
        }

        long elapsed = clock.millis() - lockTimestamp;
        if (elapsed > LOCK_TIME_MS) {
            log.info("Lock expired for user: {}", username);
            loginSucceeded(username); // очищаємо стан
            return false;
        }

        log.warn("User '{}' is currently locked ({} ms left)", username, Math.max(0, LOCK_TIME_MS - elapsed));
        return true;
    }


    public void setClock(Clock clock) {
        this.clock = clock;
    }
}
