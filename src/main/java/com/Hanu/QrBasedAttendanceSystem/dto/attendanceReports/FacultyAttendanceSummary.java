package com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class FacultyAttendanceSummary {

    private String facultyId;

    private String facultyName;

    private long totalSessions;

    private long totalAttendanceRecords;

    private List<StudentAttendanceSummary> studentAttendanceSummaries;
}
