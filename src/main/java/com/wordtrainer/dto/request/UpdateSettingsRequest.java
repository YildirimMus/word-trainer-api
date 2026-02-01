package com.wordtrainer.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSettingsRequest {
    
    @Min(1) @Max(10)
    private Integer flashDuration;
    
    @Min(0) @Max(2)
    private Double speechRate;
    
    @Min(0) @Max(5)
    private Integer autoRepeat;
    
    @Min(0) @Max(50)
    private Integer wordsPerSession;
    
    private String wordOrder;
    
    private Boolean showCorrection;
}
