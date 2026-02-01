package com.wordtrainer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "L'email est requis")
    private String email;
    
    @NotBlank(message = "Le mot de passe est requis")
    private String password;
}
