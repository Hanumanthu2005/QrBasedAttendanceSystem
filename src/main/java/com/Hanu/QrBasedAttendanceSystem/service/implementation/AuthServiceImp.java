package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.dto.auth.LoginRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.auth.LoginResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.User;
import com.Hanu.QrBasedAttendanceSystem.repo.UserRepository;
import com.Hanu.QrBasedAttendanceSystem.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthServiceImp implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Email or Password"));

        boolean verification = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!verification) {
            throw new BadCredentialsException("Invalid Email or Password");
        }

        return LoginResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .role(String.valueOf(user.getRole()))
                .userId(user.getId())
                .build();
    }
}
