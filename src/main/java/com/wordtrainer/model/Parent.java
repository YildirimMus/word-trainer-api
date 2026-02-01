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
@Document(collection = "parents")
public class Parent {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    private String passwordHash;
    
    private String firstName;
    
    @Builder.Default
    private Subscription subscription = new Subscription();
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subscription {
        @Builder.Default
        private String plan = "free";
        private Instant expiresAt;
    }
}
