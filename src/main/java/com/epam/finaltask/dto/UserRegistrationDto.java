package com.epam.finaltask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UserRegistrationDto {

    @NotBlank(message = "{validation.username.required}")
    @Size(min = 3, max = 30, message = "{validation.username.length}")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])[A-Za-z0-9]+$",
            message = "{validation.username.pattern}"
    )
    private String username;

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 50, message = "{validation.email.length}")
    private String email;

    @Size(max = 25, message = "{validation.phone.length}")
    @Pattern(
            regexp = "^$|^(\\+\\d{1,3}( )?)?\\d{7,15}$",
            message = "{validation.phone.invalid}"
    )
    private String phoneNumber;

    @NotBlank(message = "{validation.password.required}")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{7,64}$",
            message = "{validation.password.rules}"
    )
    private String password;

    @NotBlank(message = "{validation.confirmPassword.required}")
    private String confirmPassword;

    private String role = "USER";
}
