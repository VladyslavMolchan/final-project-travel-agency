package com.epam.finaltask.controller;


import com.epam.finaltask.dto.LoginRequestDto;
import com.epam.finaltask.security.JwtUtil;
import com.epam.finaltask.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/auth")
@Slf4j
public class LoginController {

    private final AuthService authService;
    private final MessageSource messageSource;
    private final JwtUtil jwtUtil;

    public LoginController(AuthService authService, MessageSource messageSource, JwtUtil jwtUtil) {
        this.authService = authService;
        this.messageSource = messageSource;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/sign-in")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequestDto());
        }
        return "auth/sign-in";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginRequest") LoginRequestDto loginRequest,
                        BindingResult result,
                        RedirectAttributes redirectAttributes,
                        HttpServletResponse response) {

        String username = loginRequest.getUsername();

        if (result.hasErrors()) {
            log.warn("Login validation failed for user '{}': {}", username, result.getAllErrors());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginRequest", result);
            redirectAttributes.addFlashAttribute("loginRequest", loginRequest);
            return "redirect:/auth/sign-in";
        }

        boolean success = authService.login(loginRequest);

        if (!success) {
            log.warn("Login failed for user '{}'", username);
            redirectAttributes.addFlashAttribute("loginError", getMessage("login.error"));
            return "redirect:/auth/sign-in";
        }


        String token = jwtUtil.generateToken(username);
        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);

        jwtCookie.setAttribute("SameSite", "Lax");

        response.addCookie(jwtCookie);

        log.info("User '{}' logged in successfully", username);
        redirectAttributes.addFlashAttribute("loginSuccess", getMessage("login.success"));
        return "redirect:/";
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
