package com.epam.finaltask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForgotPasswordRequestDto {

    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email must be less than or equal to 50 characters")
    private String email;
}
