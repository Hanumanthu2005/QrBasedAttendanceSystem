package com.Hanu.QrBasedAttendanceSystem.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoginResponse {

    private Long userId;

    private String name;

    private String email;

    private String role;
}
