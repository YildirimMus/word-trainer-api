package com.wordtrainer.dto.response;

import com.wordtrainer.model.Child;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildResponse {
    
    private String id;
    private String parentId;
    private String firstName;
    private String username;
    private String avatar;
    private String schoolLevel;
    private Child.Settings settings;
    private Child.Stats stats;
    private Instant createdAt;
    private Instant updatedAt;
    
    public static ChildResponse fromEntity(Child child) {
        return ChildResponse.builder()
                .id(child.getId())
                .parentId(child.getParentId())
                .firstName(child.getFirstName())
                .username(child.getUsername())
                .avatar(child.getAvatar())
                .schoolLevel(child.getSchoolLevel())
                .settings(child.getSettings())
                .stats(child.getStats())
                .createdAt(child.getCreatedAt())
                .updatedAt(child.getUpdatedAt())
                .build();
    }
}
