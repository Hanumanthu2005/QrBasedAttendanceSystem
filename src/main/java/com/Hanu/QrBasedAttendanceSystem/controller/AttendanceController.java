package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports.FacultyAttendanceSummary;
import com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports.StudentAttendanceSummary;
import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceReportResponse;
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


    // =================== ADMIN =====================
    @GetMapping("/admin/attendance")
    public ResponseEntity<List<AttendanceResponse>> getAttendance(
            @RequestParam(required = false)
            Long studentId,
            @RequestParam(required = false)
            Long facultyId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {
        if(studentId == null && facultyId == null && startDate == null && endDate == null) {
            return ResponseEntity.ok(
                    attendanceService.getAllAttendance()
            );
        }

        if(studentId != null) {
            return ResponseEntity.ok(
                    attendanceService.getStudentAttendance(
                            studentId
                    ));
        }

        if(startDate != null && endDate != null) {
            if(facultyId == null) {
                return ResponseEntity.ok(
                        attendanceService.getAllAttendanceWithDateRange(
                                startDate,
                                endDate
                        ));
            }
            return ResponseEntity.ok(
                    attendanceService.getFacultyAttendanceInBetween(
                            facultyId,
                            startDate,
                            endDate
                    ));
        } else {
            if(facultyId != null) {
                return ResponseEntity.ok(
                        attendanceService.getFacultyAttendance(
                                facultyId
                        ));
            }
        }

        return ResponseEntity.badRequest().body(List.of());
    }

    // ==================== STUDENT ===================

    @GetMapping("/student/attendance")
    public ResponseEntity<List<AttendanceResponse>> getStudentAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        if(startDate != null && endDate != null) {
            return ResponseEntity.ok(attendanceService.getStudentAttendanceInBetween(startDate, endDate));
        }

        if((startDate == null) != (endDate == null)) {
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok().body(attendanceService.getStudentAttendance());
    }

    @GetMapping("/student/attendance/summary")
    public ResponseEntity<StudentAttendanceSummary> getStudentSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        if(startDate != null && endDate != null) {
            return ResponseEntity.ok(attendanceService.getStudentAttendanceSummary(startDate, endDate));
        }

        if((startDate == null) != (endDate == null)) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(attendanceService.getStudentAttendanceSummary());
    }

    // =================== FACULTY ======================

    @PostMapping("/faculty/attendance")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok().body(attendanceService.markAttendance(request));
    }

    @GetMapping("/faculty/attendance")
    public ResponseEntity<List<AttendanceResponse>> getFacultyAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        if(startDate != null && endDate != null) {
            return ResponseEntity.ok(attendanceService.getFacultyAttendanceInBetween(startDate, endDate));
        }

        if((startDate == null) != (endDate == null)) {
            return ResponseEntity.badRequest().body(List.of());
        }
        return ResponseEntity.ok().body(attendanceService.getFacultyAttendance());
    }

    @GetMapping("/faculty/attendance/summary")
    public ResponseEntity<FacultyAttendanceSummary> getFacultyAttendanceSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate ) {
        if(startDate != null && endDate != null) {
            return ResponseEntity.ok(attendanceService.getFacultyAttendanceSummary(startDate, endDate));
        }

        if((startDate == null) != (endDate == null)) {
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok(attendanceService.getFacultyAttendanceSummary());
    }

    @GetMapping("/faculty/session/{sessionId}/attendance")
    public ResponseEntity<AttendanceReportResponse> getSessionAttendance(@PathVariable Long sessionId) {
        return ResponseEntity.ok(attendanceService.getSessionAttendances(sessionId));
    }
}
