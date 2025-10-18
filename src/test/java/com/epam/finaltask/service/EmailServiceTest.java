package com.epam.finaltask.service;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void sendPasswordResetEmail_success() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MessageSource messageSource = mock(MessageSource.class);

        EmailService emailService = new EmailService(mailSender, messageSource);

        String toEmail = "test@example.com";
        String resetLink = "http://reset-link";

        when(messageSource.getMessage(eq("reset.email.subject"), any(), any())).thenReturn("Password Reset Request");
        when(messageSource.getMessage(eq("reset.email.body"), eq(new Object[]{resetLink}), any()))
                .thenReturn("Click here to reset: " + resetLink);

        emailService.sendPasswordResetEmail(toEmail, resetLink);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assertEquals(toEmail, sentMessage.getTo()[0]);
        assertEquals("Password Reset Request", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains(resetLink));
    }

    @Test
    void sendPasswordResetEmail_failure() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MessageSource messageSource = mock(MessageSource.class);

        EmailService emailService = new EmailService(mailSender, messageSource);

        String toEmail = "test@example.com";
        String resetLink = "http://reset-link";

        when(messageSource.getMessage(eq("reset.email.subject"), any(), any())).thenReturn("Password Reset Request");
        when(messageSource.getMessage(eq("reset.email.body"), eq(new Object[]{resetLink}), any()))
                .thenReturn("Click here to reset: " + resetLink);

        doThrow(new RuntimeException("SMTP server error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetEmail(toEmail, resetLink)
        );

        assertTrue(exception.getMessage().contains("Failed to send email"));
    }
}
