package com.Hanu.QrBasedAttendanceSystem.controller;

import com.Hanu.QrBasedAttendanceSystem.dto.faculty.FacultyRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.faculty.FacultyResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.faculty.UpdateFacultyRequest;
import com.Hanu.QrBasedAttendanceSystem.service.FacultyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    @PostMapping
    public ResponseEntity<FacultyResponse> createFaculty(
            @Valid @RequestBody FacultyRequest request) {
        return ResponseEntity.ok().body(facultyService.createFaculty(request));
    }

    @GetMapping
    public ResponseEntity<List<FacultyResponse>> getFaculty() {
        return ResponseEntity.ok().body(facultyService.getFaculty());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacultyResponse> getFacultyById(@PathVariable Long id) {
        return ResponseEntity.ok().body(facultyService.getFacultyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyResponse> updateFaculty(@PathVariable Long id, @Valid @RequestBody UpdateFacultyRequest request) {
        return ResponseEntity.ok().body(facultyService.updateFaculty(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FacultyResponse> deleteFaculty(@PathVariable Long id) {
        return ResponseEntity.ok().body(facultyService.deleteFaculty(id));
    }
}
