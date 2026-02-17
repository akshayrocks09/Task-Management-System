package com.assignment.TaskManagementSystem.service;

import com.assignment.TaskManagementSystem.dto.AuthResponse;
import com.assignment.TaskManagementSystem.dto.LoginRequest;
import com.assignment.TaskManagementSystem.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
