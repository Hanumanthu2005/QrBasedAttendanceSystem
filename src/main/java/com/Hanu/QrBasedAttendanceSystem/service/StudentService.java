package com.Hanu.QrBasedAttendanceSystem.service;

import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.student.UpdateStudentRequest;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(StudentRequest request);

    List<StudentResponse> getAllStudents();

    StudentResponse getStudentById(Long id);

    StudentResponse updateStudent(Long id, @Valid UpdateStudentRequest request);

    @Nullable StudentResponse deleteStudent(Long id);
}
