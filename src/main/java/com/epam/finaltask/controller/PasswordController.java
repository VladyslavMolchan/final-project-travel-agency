package com.epam.finaltask.controller;

import com.epam.finaltask.dto.ForgotPasswordRequestDto;
import com.epam.finaltask.dto.ResetPasswordDto;
import com.epam.finaltask.model.User;
import com.epam.finaltask.service.AuthService;
import com.epam.finaltask.service.EmailService;
import com.epam.finaltask.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


@Controller
@RequestMapping("/auth")
@Slf4j
public class PasswordController {

    private final PasswordResetService passwordResetService;
    private final EmailService emailService;
    private final AuthService authService;
    private final MessageSource messageSource;

    public PasswordController(PasswordResetService passwordResetService,
                              EmailService emailService,
                              AuthService authService,
                              MessageSource messageSource) {
        this.passwordResetService = passwordResetService;
        this.emailService = emailService;
        this.authService = authService;
        this.messageSource = messageSource;
    }

    // --- Forgot password ---
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        if (!model.containsAttribute("forgotPasswordRequest")) {
            model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequestDto());
        }
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @ModelAttribute("forgotPasswordRequest") @Valid ForgotPasswordRequestDto request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.forgotPasswordRequest", result);
            redirectAttributes.addFlashAttribute("forgotPasswordRequest", request);
            return "redirect:/auth/forgot-password";
        }

        String token = UUID.randomUUID().toString();
        passwordResetService.createPasswordResetToken(request.getEmail(), token);

        String resetLink = "https://localhost:8443/auth/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(request.getEmail(), resetLink);

        log.info("Password reset token generated and email sent to '{}'", request.getEmail());
        redirectAttributes.addFlashAttribute("message", getMessage("reset.link.sent"));
        return "redirect:/auth/forgot-password";
    }

    // --- Reset password ---
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        Optional<User> userOpt = passwordResetService.validatePasswordResetToken(token);

        if (userOpt.isEmpty()) {
            log.warn("Invalid or expired password reset token accessed: {}", token);
            model.addAttribute("error", getMessage("reset.password.invalid"));
            return "auth/reset-password";
        }

        if (!model.containsAttribute("resetPassword")) {
            model.addAttribute("resetPassword", new ResetPasswordDto());
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam("token") String token,
                                      @Valid @ModelAttribute("resetPassword") ResetPasswordDto resetPassword,
                                      BindingResult result,
                                      RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            log.warn("Reset password validation failed for token '{}': {}", token, result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.resetPassword", result);
            redirectAttributes.addFlashAttribute("resetPassword", resetPassword);
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/auth/reset-password?token=" + token;
        }

        if (!resetPassword.getPassword().equals(resetPassword.getConfirmPassword())) {
            log.warn("Password confirmation mismatch for token '{}'", token);
            redirectAttributes.addFlashAttribute("error", getMessage("reset.password.mismatch"));
            redirectAttributes.addFlashAttribute("resetPassword", resetPassword);
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/auth/reset-password?token=" + token;
        }

        Optional<User> userOpt = passwordResetService.validatePasswordResetToken(token);
        if (userOpt.isEmpty()) {
            log.warn("Invalid or expired password reset token used for reset: {}", token);
            redirectAttributes.addFlashAttribute("error", getMessage("reset.password.invalid"));
            return "redirect:/auth/reset-password?token=" + token;
        }

        User user = userOpt.get();
        authService.updatePassword(user, resetPassword.getPassword());

        log.info("Password successfully reset for user '{}'", user.getUsername());
        redirectAttributes.addFlashAttribute("message", getMessage("reset.password.success"));
        return "redirect:/auth/sign-in";
    }

    private String getMessage(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, null, locale);
    }
}
