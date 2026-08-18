package com.Hanu.QrBasedAttendanceSystem.dto.student;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStudentRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Faculty Id is required")
    private String facultyId;

    @NotBlank(message = "Roll no is required")
    private String roll;
}
