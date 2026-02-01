package com.wordtrainer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "training_sessions")
public class TrainingSession {
    
    @Id
    private String id;
    
    @Indexed
    private String childId;
    
    @Indexed
    private String listId;
    
    private String listName;
    
    private String trainingType;
    
    private Integer totalWords;
    
    private Integer correctCount;
    
    private Integer incorrectCount;
    
    private Integer score;
    
    private Integer durationSeconds;
    
    @Builder.Default
    private List<Result> results = new ArrayList<>();
    
    @CreatedDate
    private Instant createdAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Result {
        private String word;
        private String userAnswer;
        private Boolean correct;
    }
}
