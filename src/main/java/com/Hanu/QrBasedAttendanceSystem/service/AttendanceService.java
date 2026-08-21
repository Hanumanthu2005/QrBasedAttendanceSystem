package com.Hanu.QrBasedAttendanceSystem.service;

import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import org.jspecify.annotations.Nullable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    AttendanceResponse markAttendance(AttendanceRequest request);

    List<AttendanceResponse> getStudentAttendance();

    List<AttendanceResponse> getFacultyAttendance();

    List<AttendanceResponse> getStudentAttendanceInBetween(LocalDate startDate, LocalDate endDate);

    List<AttendanceResponse> getFacultyAttendanceInBetween(LocalDate startDate, LocalDate endDate);
}
