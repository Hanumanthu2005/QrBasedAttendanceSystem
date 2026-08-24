package com.Hanu.QrBasedAttendanceSystem.dto.session;

import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.SessionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class AttendanceReportResponse {

    private Long sessionId;

    private String facultyId;

    private LocalDate date;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private SessionStatus status;

    private List<AttendanceResponse> attendances;
}
