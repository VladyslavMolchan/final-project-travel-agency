package com.epam.finaltask.controller.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.epam.finaltask.controller.RegistrationController;
import com.epam.finaltask.dto.UserRegistrationDto;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

class RegistrationControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private RegistrationController registrationController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    void showRegistrationForm_ShouldAddUserAttributeIfMissing() throws Exception {
        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void registerUser_WithValidationErrors_ShouldReturnRegisterView() {
        UserRegistrationDto dto = new UserRegistrationDto();
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);
        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        String view = registrationController.registerUser(dto, bindingResult, model, redirectAttributes);

        verify(bindingResult).hasErrors();
        assertEquals("auth/register", view);
    }

    @Test
    void registerUser_PasswordMismatch_ShouldReturnRegisterViewWithError() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setPassword("pass1");
        dto.setConfirmPassword("pass2");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(messageSource.getMessage(eq("register.passwords.mismatch"), any(), any()))
                .thenReturn("Passwords do not match");

        String view = registrationController.registerUser(dto, bindingResult, model, redirectAttributes);

        verify(model).addAttribute(eq("registerError"), eq("Passwords do not match"));
        assertEquals("auth/register", view);
    }

    @Test
    void registerUser_AuthServiceFailure_ShouldReturnRegisterViewWithError() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(authService.register(any(), any())).thenReturn(false);

        Model model = mock(Model.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(messageSource.getMessage(eq("register.error"), any(), any()))
                .thenReturn("Registration error");

        String view = registrationController.registerUser(dto, bindingResult, model, redirectAttributes);

        verify(model).addAttribute(eq("registerError"), eq("Registration error"));
        assertEquals("auth/register", view);
    }

    @Test
    void registerUser_Success_ShouldRedirectToHomeWithSuccessFlash() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        when(authService.register(any(), any())).thenReturn(true);

        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        when(messageSource.getMessage(eq("register.success"), any(), any()))
                .thenReturn("Registration successful");

        String view = registrationController.registerUser(dto, bindingResult, null, redirectAttributes);

        verify(authService).register(eq(dto), eq(Role.USER));
        verify(redirectAttributes).addFlashAttribute(eq("registerSuccess"), eq("Registration successful"));
        assertEquals("redirect:/", view);
    }
}
