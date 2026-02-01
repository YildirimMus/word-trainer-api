package com.wordtrainer.controller;

import com.wordtrainer.dto.request.CreateListRequest;
import com.wordtrainer.dto.response.ApiResponse;
import com.wordtrainer.model.WordList;
import com.wordtrainer.service.ListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ListController {

    private final ListService listService;

    public ListController(ListService listService) {
        this.listService = listService;
    }

    @GetMapping("/children/{childId}/lists")
    public ResponseEntity<ApiResponse<List<WordList>>> getLists(
            Authentication auth,
            @PathVariable String childId) {
        List<WordList> lists = listService.getListsForChild(childId);
        return ResponseEntity.ok(ApiResponse.success(lists));
    }

    @PostMapping("/children/{childId}/lists")
    public ResponseEntity<ApiResponse<WordList>> createList(
            Authentication auth,
            @PathVariable String childId,
            @Valid @RequestBody CreateListRequest request) {
        String role = getRole(auth);
        WordList list = listService.createList(childId, auth.getName(), role, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(list, "Liste créée avec succès"));
    }

    @GetMapping("/lists/{id}")
    public ResponseEntity<ApiResponse<WordList>> getList(@PathVariable String id) {
        WordList list = listService.getList(id);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PutMapping("/lists/{id}")
    public ResponseEntity<ApiResponse<WordList>> updateList(
            Authentication auth,
            @PathVariable String id,
            @Valid @RequestBody CreateListRequest request) {
        String role = getRole(auth);
        WordList list = listService.updateList(id, auth.getName(), role, request);
        return ResponseEntity.ok(ApiResponse.success(list, "Liste mise à jour"));
    }

    @DeleteMapping("/lists/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteList(
            Authentication auth,
            @PathVariable String id) {
        String role = getRole(auth);
        listService.deleteList(id, auth.getName(), role);
        return ResponseEntity.ok(ApiResponse.success(null, "Liste supprimée"));
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
