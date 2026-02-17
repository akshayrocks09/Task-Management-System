package com.assignment.TaskManagementSystem.service.impl;

import com.assignment.TaskManagementSystem.dto.AuthResponse;
import com.assignment.TaskManagementSystem.dto.LoginRequest;
import com.assignment.TaskManagementSystem.dto.RegisterRequest;
import com.assignment.TaskManagementSystem.entity.Role;
import com.assignment.TaskManagementSystem.entity.User;
import com.assignment.TaskManagementSystem.exception.DuplicateResourceException;
import com.assignment.TaskManagementSystem.exception.UnauthorizedException;
import com.assignment.TaskManagementSystem.repository.UserRepository;
import com.assignment.TaskManagementSystem.security.JwtService;
import com.assignment.TaskManagementSystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String jwtToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new UnauthorizedException("Invalid email or password");
        }

        String jwtToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return AuthResponse.builder()
                .token(jwtToken)
                .build();
    }
}