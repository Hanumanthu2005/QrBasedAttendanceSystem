package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.auth.LoginRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.auth.LoginResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.User;
import com.Hanu.QrBasedAttendanceSystem.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<LoginResponse> me(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        LoginResponse response = LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(response);
    }

}
