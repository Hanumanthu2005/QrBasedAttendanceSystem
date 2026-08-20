package com.Hanu.QrBasedAttendanceSystem.service;

import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;

import java.util.List;

public interface AttendanceService {
    AttendanceResponse markAttendance(AttendanceRequest request);

    List<AttendanceResponse> getStudentAttendance();

    List<AttendanceResponse> getFacultyAttendance();
}
