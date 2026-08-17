package com.Hanu.QrBasedAttendanceSystem.dto.faculty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FacultyResponse {

    private Long id;

    private String name;

    private String facultyId;

    private String email;

    private String status;
}
