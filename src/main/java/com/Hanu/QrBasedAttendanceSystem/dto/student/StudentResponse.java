package com.Hanu.QrBasedAttendanceSystem.dto.student;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StudentResponse {

    private Long id;

    private String name;

    private String email;

    private String roll;

    private String status;

    private String facultyId;
}
