package com.epam.finaltask.controller.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epam.finaltask.controller.PasswordController;
import com.epam.finaltask.dto.ForgotPasswordRequestDto;
import com.epam.finaltask.dto.ResetPasswordDto;
import com.epam.finaltask.model.User;
import com.epam.finaltask.service.AuthService;
import com.epam.finaltask.service.EmailService;
import com.epam.finaltask.service.PasswordResetService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

class PasswordControllerTest {

    @Mock
    private PasswordResetService passwordResetService;

    @Mock
    private EmailService emailService;

    @Mock
    private AuthService authService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private PasswordController passwordController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(passwordController).build();
    }

    @Test
    void showForgotPasswordForm_ShouldAddAttributeIfMissing() throws Exception {
        mockMvc.perform(get("/auth/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/forgot-password"))
                .andExpect(model().attributeExists("forgotPasswordRequest"));
    }

    @Test
    void handleForgotPassword_WithValidationErrors_ShouldRedirectBack() {
        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto();
        dto.setEmail("invalid-email");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = passwordController.handleForgotPassword(dto, bindingResult, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("org.springframework.validation.BindingResult.forgotPasswordRequest"), eq(bindingResult));
        verify(redirectAttributes).addFlashAttribute(eq("forgotPasswordRequest"), eq(dto));
        assertEquals("redirect:/auth/forgot-password", view);
    }

    @Test
    void handleForgotPassword_ValidRequest_ShouldSendEmailAndRedirect() {
        ForgotPasswordRequestDto dto = new ForgotPasswordRequestDto();
        dto.setEmail("user@example.com");


        User user = new User();
        user.setEmail("user@example.com");
        when(authService.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(messageSource.getMessage(eq("reset.link.sent"), any(), any(Locale.class))).thenReturn("Reset link sent");

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = passwordController.handleForgotPassword(dto, bindingResult, redirectAttributes);

        verify(passwordResetService).createPasswordResetToken(eq(dto.getEmail()), anyString());
        verify(emailService).sendPasswordResetEmail(eq(dto.getEmail()), contains("https://localhost:8443/auth/reset-password?token="));
        verify(redirectAttributes).addFlashAttribute(eq("message"), eq("Reset link sent"));
        assertEquals("redirect:/auth/forgot-password", view);
    }

    @Test
    void showResetPasswordForm_InvalidToken_ShouldShowError() {
        String token = UUID.randomUUID().toString();

        when(passwordResetService.validatePasswordResetToken(eq(token))).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("reset.password.invalid"), any(), any(Locale.class))).thenReturn("Invalid or expired token");

        Model model = mock(Model.class);

        String view = passwordController.showResetPasswordForm(token, model);

        verify(model).addAttribute(eq("error"), eq("Invalid or expired token"));
        assertEquals("auth/reset-password", view);
    }


    @Test
    void showResetPasswordForm_ValidToken_ShouldShowForm() {
        String token = UUID.randomUUID().toString();
        User user = new User();
        when(passwordResetService.validatePasswordResetToken(eq(token))).thenReturn(Optional.of(user));

        Model model = mock(Model.class);
        when(model.containsAttribute(eq("resetPassword"))).thenReturn(false);

        String view = passwordController.showResetPasswordForm(token, model);

        verify(model).addAttribute(eq("resetPassword"), any());
        verify(model).addAttribute(eq("token"), eq(token));
        assertEquals("auth/reset-password", view);
    }

    @Test
    void handleResetPassword_WithValidationErrors_ShouldRedirectBack() {
        String token = UUID.randomUUID().toString();
        ResetPasswordDto resetPassword = new ResetPasswordDto();

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = passwordController.handleResetPassword(token, resetPassword, bindingResult, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("org.springframework.validation.BindingResult.resetPassword"), eq(bindingResult));
        verify(redirectAttributes).addFlashAttribute(eq("resetPassword"), eq(resetPassword));
        verify(redirectAttributes).addFlashAttribute(eq("token"), eq(token));
        assertEquals("redirect:/auth/reset-password?token=" + token, view);
    }

    @Test
    void handleResetPassword_PasswordMismatch_ShouldRedirectBackWithError() {
        String token = UUID.randomUUID().toString();
        ResetPasswordDto resetPassword = new ResetPasswordDto();
        resetPassword.setPassword("password1");
        resetPassword.setConfirmPassword("password2");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);
        when(messageSource.getMessage(eq("reset.password.mismatch"), any(), any(Locale.class))).thenReturn("Passwords do not match");

        String view = passwordController.handleResetPassword(token, resetPassword, bindingResult, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Passwords do not match"));
        verify(redirectAttributes).addFlashAttribute(eq("resetPassword"), eq(resetPassword));
        verify(redirectAttributes).addFlashAttribute(eq("token"), eq(token));
        assertEquals("redirect:/auth/reset-password?token=" + token, view);
    }

    @Test
    void handleResetPassword_InvalidToken_ShouldRedirectBackWithError() {
        String token = UUID.randomUUID().toString();
        ResetPasswordDto resetPassword = new ResetPasswordDto();
        resetPassword.setPassword("password");
        resetPassword.setConfirmPassword("password");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(passwordResetService.validatePasswordResetToken(eq(token))).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("reset.password.invalid"), any(), any(Locale.class))).thenReturn("Invalid token");

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = passwordController.handleResetPassword(token, resetPassword, bindingResult, redirectAttributes);

        verify(redirectAttributes).addFlashAttribute(eq("error"), eq("Invalid token"));
        assertEquals("redirect:/auth/reset-password?token=" + token, view);
    }

    @Test
    void handleResetPassword_ValidRequest_ShouldUpdatePasswordAndRedirect() {
        String token = UUID.randomUUID().toString();
        ResetPasswordDto resetPassword = new ResetPasswordDto();
        resetPassword.setPassword("password");
        resetPassword.setConfirmPassword("password");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        User user = new User();
        user.setUsername("user1");
        when(passwordResetService.validatePasswordResetToken(eq(token))).thenReturn(Optional.of(user));
        when(messageSource.getMessage(eq("reset.password.success"), any(), any(Locale.class))).thenReturn("Password reset successful");

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = passwordController.handleResetPassword(token, resetPassword, bindingResult, redirectAttributes);

        verify(authService).updatePassword(eq(user), eq("password"));
        verify(redirectAttributes).addFlashAttribute(eq("message"), eq("Password reset successful"));
        assertEquals("redirect:/auth/sign-in", view);
    }
}
