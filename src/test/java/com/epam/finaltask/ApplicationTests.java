package com.epam.finaltask;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "application.security.jwt.refresh-token.expiration=604800000"
})
class ApplicationTests {

  @MockBean
  private JavaMailSender mailSender;

  @Test
  void contextLoads() {}
}
