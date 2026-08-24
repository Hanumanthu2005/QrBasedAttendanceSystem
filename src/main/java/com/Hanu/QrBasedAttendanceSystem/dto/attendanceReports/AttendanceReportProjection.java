package com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports;

import com.Hanu.QrBasedAttendanceSystem.entity.utils.AttendStatus;

import java.time.LocalDateTime;

public interface AttendanceReportProjection {

    Long getStudentId();

    String getStudentName();

    String getStudentRoll();

    Long getAttendanceId();

    LocalDateTime getAttendanceTime();

    AttendStatus getStatus();
}