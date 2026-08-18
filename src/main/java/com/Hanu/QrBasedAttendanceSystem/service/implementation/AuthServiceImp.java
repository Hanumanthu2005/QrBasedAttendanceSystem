package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.entity.Role;
import com.Hanu.QrBasedAttendanceSystem.entity.Status;
import com.Hanu.QrBasedAttendanceSystem.security.JwtService;
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
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Email or Password"));

        if((user.getFaculty() != null && user.getFaculty().getStatus().equals(Status.INACTIVE)) || (user.getStudent() != null && user.getStudent().getStatus().equals(Status.INACTIVE))) {
            throw new BadCredentialsException("your profile was inactive please connect with the admin");
        }

        if(!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {
            throw new BadCredentialsException(
                    "Invalid Email or Password"
            );
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .userId(user.getId())
                .build();
    }
}
