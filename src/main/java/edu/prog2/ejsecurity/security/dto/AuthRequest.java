package edu.prog2.ejsecurity.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthRequest(
        @NotBlank(message = "Email requerido")
        @Email(message = "Formato email inválido")
        String username,  // usamos email como username

        @NotBlank
        @Size(min = 8, max = 50, message = "Password: 8 a 50 caracteres")
        String password

) { }
