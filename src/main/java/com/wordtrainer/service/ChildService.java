package com.wordtrainer.service;

import com.wordtrainer.dto.request.CreateChildRequest;
import com.wordtrainer.dto.request.UpdateChildRequest;
import com.wordtrainer.dto.request.UpdateSettingsRequest;
import com.wordtrainer.dto.response.ChildResponse;
import com.wordtrainer.exception.ResourceNotFoundException;
import com.wordtrainer.exception.UnauthorizedException;
import com.wordtrainer.exception.UsernameAlreadyExistsException;
import com.wordtrainer.model.Child;
import com.wordtrainer.repository.ChildRepository;
import com.wordtrainer.repository.TrainingSessionRepository;
import com.wordtrainer.repository.WordListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChildService {

    private static final Logger log = LoggerFactory.getLogger(ChildService.class);

    private final ChildRepository childRepository;
    private final WordListRepository listRepository;
    private final TrainingSessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;

    public ChildService(ChildRepository childRepository, WordListRepository listRepository,
                        TrainingSessionRepository sessionRepository, PasswordEncoder passwordEncoder) {
        this.childRepository = childRepository;
        this.listRepository = listRepository;
        this.sessionRepository = sessionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ChildResponse> getChildrenForParent(String parentId) {
        return childRepository.findByParentId(parentId)
                .stream()
                .map(ChildResponse::fromEntity)
                .toList();
    }

    public ChildResponse getChild(String childId, String parentId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));

        if (!child.getParentId().equals(parentId)) {
            throw new UnauthorizedException("Accès non autorisé à cet enfant");
        }

        return ChildResponse.fromEntity(child);
    }

    public ChildResponse createChild(String parentId, CreateChildRequest request) {
        String username = request.getUsername().toLowerCase().trim();
        if (childRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException("Ce nom d'utilisateur est déjà pris");
        }

        Child child = Child.builder()
                .parentId(parentId)
                .firstName(request.getFirstName().trim())
                .username(username)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .avatar(request.getAvatar())
                .schoolLevel(request.getSchoolLevel())
                .build();

        child = childRepository.save(child);
        log.info("New child created: {} for parent: {}", child.getUsername(), parentId);

        return ChildResponse.fromEntity(child);
    }

    public ChildResponse updateChild(String childId, String parentId, UpdateChildRequest request) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));

        if (!child.getParentId().equals(parentId)) {
            throw new UnauthorizedException("Accès non autorisé à cet enfant");
        }

        String newUsername = request.getUsername().toLowerCase().trim();
        if (!child.getUsername().equals(newUsername) && childRepository.existsByUsername(newUsername)) {
            throw new UsernameAlreadyExistsException("Ce nom d'utilisateur est déjà pris");
        }

        child.setFirstName(request.getFirstName().trim());
        child.setUsername(newUsername);
        
        if (request.getAvatar() != null) {
            child.setAvatar(request.getAvatar());
        }
        if (request.getSchoolLevel() != null) {
            child.setSchoolLevel(request.getSchoolLevel());
        }
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            child.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        child = childRepository.save(child);
        log.info("Child updated: {}", child.getUsername());

        return ChildResponse.fromEntity(child);
    }

    @Transactional
    public void deleteChild(String childId, String parentId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));

        if (!child.getParentId().equals(parentId)) {
            throw new UnauthorizedException("Accès non autorisé à cet enfant");
        }

        sessionRepository.deleteByChildId(childId);
        listRepository.deleteByChildId(childId);
        childRepository.delete(child);

        log.info("Child deleted: {}", childId);
    }

    public Child.Settings getSettings(String childId, String parentId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));

        if (!child.getParentId().equals(parentId)) {
            throw new UnauthorizedException("Accès non autorisé à cet enfant");
        }

        return child.getSettings();
    }

    public Child.Settings updateSettings(String childId, String parentId, UpdateSettingsRequest request) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));

        if (!child.getParentId().equals(parentId)) {
            throw new UnauthorizedException("Accès non autorisé à cet enfant");
        }

        Child.Settings settings = child.getSettings();
        
        if (request.getFlashDuration() != null) {
            settings.setFlashDuration(request.getFlashDuration());
        }
        if (request.getSpeechRate() != null) {
            settings.setSpeechRate(request.getSpeechRate());
        }
        if (request.getAutoRepeat() != null) {
            settings.setAutoRepeat(request.getAutoRepeat());
        }
        if (request.getWordsPerSession() != null) {
            settings.setWordsPerSession(request.getWordsPerSession());
        }
        if (request.getWordOrder() != null) {
            settings.setWordOrder(request.getWordOrder());
        }
        if (request.getShowCorrection() != null) {
            settings.setShowCorrection(request.getShowCorrection());
        }

        child.setSettings(settings);
        childRepository.save(child);

        log.info("Settings updated for child: {}", childId);
        return settings;
    }

    public Child.Settings resetSettings(String childId, String parentId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Enfant", childId));

        if (!child.getParentId().equals(parentId)) {
            throw new UnauthorizedException("Accès non autorisé à cet enfant");
        }

        child.setSettings(new Child.Settings());
        childRepository.save(child);

        log.info("Settings reset for child: {}", childId);
        return child.getSettings();
    }
}
