package com.wordtrainer.service;

import com.wordtrainer.dto.request.ChildLoginRequest;
import com.wordtrainer.dto.request.LoginRequest;
import com.wordtrainer.dto.request.RegisterRequest;
import com.wordtrainer.dto.response.AuthResponse;
import com.wordtrainer.exception.UnauthorizedException;
import com.wordtrainer.exception.UsernameAlreadyExistsException;
import com.wordtrainer.model.Child;
import com.wordtrainer.model.Parent;
import com.wordtrainer.repository.ChildRepository;
import com.wordtrainer.repository.ParentRepository;
import com.wordtrainer.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final ParentRepository parentRepository;
    private final ChildRepository childRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(ParentRepository parentRepository, ChildRepository childRepository,
                       PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.parentRepository = parentRepository;
        this.childRepository = childRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse register(RegisterRequest request) {
        if (parentRepository.existsByEmail(request.getEmail().toLowerCase())) {
            throw new UsernameAlreadyExistsException("Cet email est déjà utilisé");
        }

        Parent parent = Parent.builder()
                .email(request.getEmail().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .build();

        parent = parentRepository.save(parent);
        log.info("New parent registered: {}", parent.getEmail());

        return buildAuthResponse(parent);
    }

    public AuthResponse login(LoginRequest request) {
        Parent parent = parentRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), parent.getPasswordHash())) {
            throw new UnauthorizedException("Email ou mot de passe incorrect");
        }

        log.info("Parent logged in: {}", parent.getEmail());
        return buildAuthResponse(parent);
    }

    public AuthResponse loginChild(ChildLoginRequest request) {
        Child child = childRepository.findByUsername(request.getUsername().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Nom d'utilisateur ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getPassword(), child.getPasswordHash())) {
            throw new UnauthorizedException("Nom d'utilisateur ou mot de passe incorrect");
        }

        log.info("Child logged in: {}", child.getUsername());
        return buildAuthResponse(child);
    }

    private AuthResponse buildAuthResponse(Parent parent) {
        String token = tokenProvider.generateToken(parent.getId(), "parent");
        String refreshToken = tokenProvider.generateRefreshToken(parent.getId());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(parent.getId())
                        .email(parent.getEmail())
                        .firstName(parent.getFirstName())
                        .role("parent")
                        .build())
                .build();
    }

    private AuthResponse buildAuthResponse(Child child) {
        String token = tokenProvider.generateToken(child.getId(), "child");
        String refreshToken = tokenProvider.generateRefreshToken(child.getId());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .type("Bearer")
                .user(AuthResponse.UserInfo.builder()
                        .id(child.getId())
                        .firstName(child.getFirstName())
                        .username(child.getUsername())
                        .avatar(child.getAvatar())
                        .schoolLevel(child.getSchoolLevel())
                        .role("child")
                        .build())
                .build();
    }
}
