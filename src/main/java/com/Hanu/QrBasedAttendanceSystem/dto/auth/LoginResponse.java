package com.Hanu.QrBasedAttendanceSystem.dto.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private Long userId;

    private String name;

    private String email;

    private String role;
}
