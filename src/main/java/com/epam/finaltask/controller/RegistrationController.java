package com.epam.finaltask.controller;


import com.epam.finaltask.dto.UserRegistrationDto;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;



@Controller
@RequestMapping("/auth")
@Slf4j
public class RegistrationController {

    private final AuthService authService;
    private final MessageSource messageSource;

    public RegistrationController(AuthService authService, MessageSource messageSource) {
        this.authService = authService;
        this.messageSource = messageSource;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", new UserRegistrationDto());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            log.warn("Registration validation errors: {}", result.getAllErrors());
            return "auth/register";
        }

        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            log.warn("Password mismatch for user '{}'", registrationDto.getUsername());
            model.addAttribute("registerError", getMessage("register.passwords.mismatch"));
            return "auth/register";
        }

        boolean success = authService.register(registrationDto, Role.USER);
        if (!success) {
            log.warn("Registration failed for user '{}'", registrationDto.getUsername());
            model.addAttribute("registerError", getMessage("register.error"));
            return "auth/register";
        }

        log.info("User '{}' successfully registered", registrationDto.getUsername());
        redirectAttributes.addFlashAttribute("registerSuccess", getMessage("register.success"));
        return "redirect:/";
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
