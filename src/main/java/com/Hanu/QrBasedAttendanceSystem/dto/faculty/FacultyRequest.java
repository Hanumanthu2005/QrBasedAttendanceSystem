package com.Hanu.QrBasedAttendanceSystem.dto.faculty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid Email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contains 8 characters")
    private String password;

    @NotBlank(message = "Faculty id is required")
    private String facultyId;
}