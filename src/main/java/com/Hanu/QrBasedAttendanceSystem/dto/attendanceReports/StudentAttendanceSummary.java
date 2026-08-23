package com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentAttendanceSummary {

    private String name;

    private String roll;

    private long totalSessions;

    private long presentSessions;

    private long absentSessions;

    private double attendancePercentage;
}
