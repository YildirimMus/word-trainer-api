package com.wordtrainer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChildRequest {
    
    @NotBlank(message = "Le prénom est requis")
    private String firstName;
    
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 30, message = "Le nom d'utilisateur doit contenir entre 3 et 30 caractères")
    private String username;
    
    private String avatar;
    
    private String schoolLevel;
    
    @Size(min = 4, message = "Le mot de passe doit contenir au moins 4 caractères")
    private String newPassword;
}
