package com.epam.finaltask.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "{validation.username.required}")
    @Size(min = 3, max = 30, message = "{validation.username.length}")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])[A-Za-z0-9]+$",
            message = "{validation.username.pattern}"
    )
    private String username;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 7, max = 64, message = "{validation.password.rules}")
    private String password;
}
