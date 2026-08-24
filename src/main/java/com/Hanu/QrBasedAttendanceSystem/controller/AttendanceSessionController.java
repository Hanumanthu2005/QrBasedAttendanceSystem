package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceReportResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceSessionResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.AttendanceSession;
import com.Hanu.QrBasedAttendanceSystem.service.AttendanceSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/faculty/session")
    public ResponseEntity<AttendanceSessionResponse> getActiveSession() {
        return ResponseEntity.ok(attendanceSessionService.getActiveSession());
    }

    @PostMapping("/faculty/session/{sessionId}/close")
    public ResponseEntity<AttendanceSessionResponse> closeSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceSessionService.closeSession(sessionId));
    }

    @GetMapping("/faculty/sessions")
    public ResponseEntity<List<AttendanceSessionResponse>> getAllSessions() {
        return ResponseEntity.ok(attendanceSessionService.getAllSession());
    }
}
