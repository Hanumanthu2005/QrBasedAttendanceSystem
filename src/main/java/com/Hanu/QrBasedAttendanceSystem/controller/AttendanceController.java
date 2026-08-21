package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import com.Hanu.QrBasedAttendanceSystem.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        if(startDate != null && endDate != null) {
            return ResponseEntity.ok(attendanceService.getStudentAttendanceInBetween(startDate, endDate));
        }

        if((startDate == null) != (endDate == null)) {
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok().body(attendanceService.getStudentAttendance());
    }

    @GetMapping("/faculty/attendance")
    public ResponseEntity<List<AttendanceResponse>> getFacultyAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        if(startDate != null && endDate != null) {
            return ResponseEntity.ok(attendanceService.getFacultyAttendanceInBetween(startDate, endDate));
        }

        if((startDate == null) != (endDate == null)) {
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok().body(attendanceService.getFacultyAttendance());
    }
}
