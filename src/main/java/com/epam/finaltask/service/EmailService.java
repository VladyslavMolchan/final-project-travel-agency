package com.epam.finaltask.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

@Slf4j
@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final MessageSource messageSource;

    public EmailService(JavaMailSender mailSender, MessageSource messageSource) {
        this.mailSender = mailSender;
        this.messageSource = messageSource;
    }

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        log.info("Sending password reset email to: {}", toEmail);

        try {
            Locale locale = LocaleContextHolder.getLocale();
            String subject = messageSource.getMessage("reset.email.subject", null, locale);
            String body = messageSource.getMessage("reset.email.body", new Object[]{resetLink}, locale);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

            log.info("Password reset email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
