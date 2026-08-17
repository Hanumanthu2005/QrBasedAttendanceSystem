package com.Hanu.QrBasedAttendanceSystem.service;

import com.Hanu.QrBasedAttendanceSystem.dto.faculty.FacultyRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.faculty.FacultyResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.faculty.UpdateFacultyRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface FacultyService {

    public FacultyResponse createFaculty(FacultyRequest request);

    @Nullable List<FacultyResponse> getFaculty();

    @Nullable FacultyResponse getFacultyById(Long id);

    @Nullable FacultyResponse updateFaculty(Long id, UpdateFacultyRequest request);

    @Nullable FacultyResponse deleteFaculty(Long id);
}
