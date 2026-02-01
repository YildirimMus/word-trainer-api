package com.wordtrainer.controller;

import com.wordtrainer.dto.request.SaveTrainingRequest;
import com.wordtrainer.dto.response.ApiResponse;
import com.wordtrainer.model.TrainingSession;
import com.wordtrainer.service.TrainingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/children/{childId}")
public class TrainingController {

    private final TrainingService trainingService;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<TrainingSession>>> getHistory(
            @PathVariable String childId,
            @RequestParam(required = false) String listId,
            @RequestParam(defaultValue = "50") int limit) {
        List<TrainingSession> sessions = trainingService.getHistory(childId, listId, limit);
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @PostMapping("/training")
    public ResponseEntity<ApiResponse<TrainingSession>> saveTraining(
            Authentication auth,
            @PathVariable String childId,
            @Valid @RequestBody SaveTrainingRequest request) {
        String role = getRole(auth);
        TrainingSession session = trainingService.saveTraining(childId, auth.getName(), role, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(session, "Entraînement enregistré"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(@PathVariable String childId) {
        Map<String, Object> stats = trainingService.getChildStats(childId);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/failed-words")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFailedWords(
            @PathVariable String childId,
            @RequestParam(required = false) String listId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> words = trainingService.getFailedWords(childId, listId, limit);
        return ResponseEntity.ok(ApiResponse.success(words));
    }

    private String getRole(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5).toLowerCase())
                .findFirst()
                .orElse("child");
    }
}
