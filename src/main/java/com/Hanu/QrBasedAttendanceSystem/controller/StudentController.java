package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.student.UpdateStudentRequest;
import com.Hanu.QrBasedAttendanceSystem.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/student")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok().body(studentService.createStudent(request));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok().body(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok().body(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request) {
        return ResponseEntity.ok().body(studentService.updateStudent(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StudentResponse> deleteStudent(@PathVariable Long id) {
        return ResponseEntity.ok().body(studentService.deleteStudent(id));
    }
}
