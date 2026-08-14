package com.Hanu.QrBasedAttendanceSystem.service;

import com.Hanu.QrBasedAttendanceSystem.dto.auth.LoginRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.auth.LoginResponse;

public interface AuthService {

    public LoginResponse login(LoginRequest request);
}
