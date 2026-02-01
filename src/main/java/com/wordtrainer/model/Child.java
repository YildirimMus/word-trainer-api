package com.wordtrainer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "children")
public class Child {
    
    @Id
    private String id;
    
    @Indexed
    private String parentId;
    
    private String firstName;
    
    @Indexed(unique = true)
    private String username;
    
    private String passwordHash;
    
    @Builder.Default
    private String avatar = "👦";
    
    @Builder.Default
    private String schoolLevel = "3p";
    
    @Builder.Default
    private Settings settings = new Settings();
    
    @Builder.Default
    private Stats stats = new Stats();
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Settings {
        @Builder.Default
        private Integer flashDuration = 3;
        
        @Builder.Default
        private Double speechRate = 1.0;
        
        @Builder.Default
        private Integer autoRepeat = 0;
        
        @Builder.Default
        private Integer wordsPerSession = 10;
        
        @Builder.Default
        private String wordOrder = "random";
        
        @Builder.Default
        private Boolean showCorrection = true;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        @Builder.Default
        private Integer totalTrainings = 0;
        
        @Builder.Default
        private Integer totalWords = 0;
        
        @Builder.Default
        private Integer correctWords = 0;
        
        @Builder.Default
        private Integer streak = 0;
        
        private Instant lastTrainingDate;
    }
}
