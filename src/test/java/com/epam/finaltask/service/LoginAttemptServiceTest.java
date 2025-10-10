package com.epam.finaltask.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

class LoginAttemptServiceTest {

    private LoginAttemptService service;
    private Clock baseClock;
    private Instant baseInstant;

    @BeforeEach
    void setUp() {
        // Фіксуємо початковий час — робимо тести детермінованими
        baseInstant = Instant.parse("2025-01-01T00:00:00Z");
        baseClock = Clock.fixed(baseInstant, ZoneOffset.UTC);


        service = new LoginAttemptService(baseClock);
    }

    @Test
    void loginSucceeded_clearsAttemptsAndLock() {
        String username = "testUser";

        service.loginFailed(username);
        service.loginFailed(username);
        service.loginFailed(username);
        assertTrue(service.isBlocked(username), "Після 3 невдалих спроб користувач має бути заблокований");

        service.loginSucceeded(username);

        assertFalse(service.isBlocked(username), "Після успішного логіну блокування має знятись");
    }

    @Test
    void loginFailed_incrementsAttemptsAndLocksAfterMax() {
        String username = "testUser";

        service.loginFailed(username);
        assertFalse(service.isBlocked(username), "Після 1 спроби не повинно бути блокування");

        service.loginFailed(username);
        assertFalse(service.isBlocked(username), "Після 2 спроб не повинно бути блокування");

        service.loginFailed(username);
        assertTrue(service.isBlocked(username), "Після 3 спроб має бути блокування");
    }

    @Test
    void isBlocked_unlocksUserAfterLockExpires() {
        String username = "user4";


        service.loginFailed(username);
        service.loginFailed(username);
        service.loginFailed(username);
        assertTrue(service.isBlocked(username), "Користувач має бути заблокований одразу після 3-ї невдалої спроби");


        Clock laterClock = Clock.fixed(baseInstant.plusSeconds(11 * 60), ZoneOffset.UTC);


        service.setClock(laterClock);


        assertFalse(service.isBlocked(username), "Після закінчення часу блокування користувач має бути розблокований");
    }

    @Test
    void loginFailed_doesNotIncreaseAttemptsWhenBlocked() {
        String username = "blockedUser";


        service.loginFailed(username);
        service.loginFailed(username);
        service.loginFailed(username);
        assertTrue(service.isBlocked(username));


        service.loginFailed(username);


        assertTrue(service.isBlocked(username));
    }
}
