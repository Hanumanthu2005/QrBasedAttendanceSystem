package com.Hanu.QrBasedAttendanceSystem.dto.attendance;


import com.Hanu.QrBasedAttendanceSystem.entity.AttendStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttendanceResponse {

    private Long id;

    private String studentName;

    private String studentRoll;

    private String facultyId;

    private LocalDate attendanceDate;

    private LocalDateTime attendanceTime;

    private AttendStatus status;
}
