package com.wordtrainer.controller;

import com.wordtrainer.dto.request.CreateChildRequest;
import com.wordtrainer.dto.request.UpdateChildRequest;
import com.wordtrainer.dto.request.UpdateSettingsRequest;
import com.wordtrainer.dto.response.ApiResponse;
import com.wordtrainer.dto.response.ChildResponse;
import com.wordtrainer.model.Child;
import com.wordtrainer.service.ChildService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children")
public class ChildController {

    private final ChildService childService;

    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    @GetMapping
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<List<ChildResponse>>> getChildren(Authentication auth) {
        String parentId = auth.getName();
        List<ChildResponse> children = childService.getChildrenForParent(parentId);
        return ResponseEntity.ok(ApiResponse.success(children));
    }

    @PostMapping
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<ChildResponse>> createChild(
            Authentication auth,
            @Valid @RequestBody CreateChildRequest request) {
        String parentId = auth.getName();
        ChildResponse child = childService.createChild(parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(child, "Enfant créé avec succès"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<ChildResponse>> getChild(
            Authentication auth,
            @PathVariable String id) {
        String parentId = auth.getName();
        ChildResponse child = childService.getChild(id, parentId);
        return ResponseEntity.ok(ApiResponse.success(child));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<ChildResponse>> updateChild(
            Authentication auth,
            @PathVariable String id,
            @Valid @RequestBody UpdateChildRequest request) {
        String parentId = auth.getName();
        ChildResponse child = childService.updateChild(id, parentId, request);
        return ResponseEntity.ok(ApiResponse.success(child, "Enfant mis à jour"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<Void>> deleteChild(
            Authentication auth,
            @PathVariable String id) {
        String parentId = auth.getName();
        childService.deleteChild(id, parentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Enfant supprimé"));
    }

    @GetMapping("/{id}/settings")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<Child.Settings>> getSettings(
            Authentication auth,
            @PathVariable String id) {
        String parentId = auth.getName();
        Child.Settings settings = childService.getSettings(id, parentId);
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping("/{id}/settings")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<Child.Settings>> updateSettings(
            Authentication auth,
            @PathVariable String id,
            @Valid @RequestBody UpdateSettingsRequest request) {
        String parentId = auth.getName();
        Child.Settings settings = childService.updateSettings(id, parentId, request);
        return ResponseEntity.ok(ApiResponse.success(settings, "Paramètres mis à jour"));
    }

    @PostMapping("/{id}/settings/reset")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<ApiResponse<Child.Settings>> resetSettings(
            Authentication auth,
            @PathVariable String id) {
        String parentId = auth.getName();
        Child.Settings settings = childService.resetSettings(id, parentId);
        return ResponseEntity.ok(ApiResponse.success(settings, "Paramètres réinitialisés"));
    }
}
