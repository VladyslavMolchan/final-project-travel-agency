package com.epam.finaltask.controller;


import com.epam.finaltask.dto.LoginRequestDto;
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
import java.util.Map;


@Controller
@RequestMapping("/auth")
@Slf4j
public class LoginController {

    private final AuthService authService;
    private final MessageSource messageSource;

    public LoginController(AuthService authService, MessageSource messageSource) {
        this.authService = authService;
        this.messageSource = messageSource;
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

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.loginRequest", result);
            redirectAttributes.addFlashAttribute("loginRequest", loginRequest);
            return "redirect:/auth/sign-in";
        }

        boolean success = authService.login(loginRequest);
        if (!success) {
            redirectAttributes.addFlashAttribute("loginError", getMessage("login.error"));
            return "redirect:/auth/sign-in";
        }


        Map<String, String> tokens = authService.generateTokens(loginRequest.getUsername());

        Cookie accessCookie = new Cookie("jwt", tokens.get("accessToken"));
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(3600);
        accessCookie.setAttribute("SameSite", "Lax");

        Cookie refreshCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(86400 * 7);
        refreshCookie.setAttribute("SameSite", "Lax");

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        redirectAttributes.addFlashAttribute("loginSuccess", getMessage("login.success"));
        return "redirect:/";
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
