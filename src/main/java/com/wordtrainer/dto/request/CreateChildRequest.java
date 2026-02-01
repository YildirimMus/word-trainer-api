package com.wordtrainer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChildRequest {
    
    @NotBlank(message = "Le prénom est requis")
    private String firstName;
    
    @NotBlank(message = "Le nom d'utilisateur est requis")
    @Size(min = 3, max = 30, message = "Le nom d'utilisateur doit contenir entre 3 et 30 caractères")
    private String username;
    
    @NotBlank(message = "Le mot de passe est requis")
    @Size(min = 4, message = "Le mot de passe doit contenir au moins 4 caractères")
    private String password;
    
    @Builder.Default
    private String avatar = "👦";
    
    @Builder.Default
    private String schoolLevel = "3p";
}
