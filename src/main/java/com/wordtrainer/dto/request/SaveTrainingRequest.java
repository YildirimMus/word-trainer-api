package com.wordtrainer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveTrainingRequest {
    
    @NotBlank(message = "L'ID de la liste est requis")
    private String listId;
    
    @NotBlank(message = "Le type d'entraînement est requis")
    private String trainingType;
    
    @NotNull
    private Integer durationSeconds;
    
    @NotEmpty(message = "Les résultats sont requis")
    private List<ResultItem> results;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultItem {
        private String word;
        private String userAnswer;
        private Boolean correct;
    }
}
