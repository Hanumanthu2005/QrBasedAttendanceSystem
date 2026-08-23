package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceSessionResponse;
import com.Hanu.QrBasedAttendanceSystem.service.AttendanceSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttendanceSessionController {

    private final AttendanceSessionService attendanceSessionService;

    @PostMapping("/faculty/session")
    public ResponseEntity<AttendanceSessionResponse> createSession() {
        return ResponseEntity.ok(
                attendanceSessionService.createSession()
        );
    }
}
