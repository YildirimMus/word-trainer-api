package com.wordtrainer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateListRequest {
    
    @NotBlank(message = "Le nom de la liste est requis")
    private String name;
    
    @NotEmpty(message = "La liste doit contenir au moins un mot")
    private List<String> words;
}
