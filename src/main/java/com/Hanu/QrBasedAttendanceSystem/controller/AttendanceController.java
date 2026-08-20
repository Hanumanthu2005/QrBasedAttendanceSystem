package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import com.Hanu.QrBasedAttendanceSystem.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/faculty/attendance")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok().body(attendanceService.markAttendance(request));
    }

    @GetMapping("/student/attendance")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance() {
        return ResponseEntity.ok().body(attendanceService.getStudentAttendance());
    }

    @GetMapping("/faculty/attendance")
    public ResponseEntity<List<AttendanceResponse>> getFacultyAttendance() {
        return ResponseEntity.ok().body(attendanceService.getFacultyAttendance());
    }
}
