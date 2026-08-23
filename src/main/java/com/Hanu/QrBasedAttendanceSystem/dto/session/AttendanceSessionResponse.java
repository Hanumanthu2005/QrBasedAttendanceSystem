package com.Hanu.QrBasedAttendanceSystem.dto.session;

import com.Hanu.QrBasedAttendanceSystem.entity.utils.SessionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AttendanceSessionResponse {

    private Long id;

    private LocalDate date;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private SessionStatus status;

    private String facultyId;
}
