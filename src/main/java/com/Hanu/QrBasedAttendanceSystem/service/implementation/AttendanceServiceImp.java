package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.RelationMismatchException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotAvailableException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.*;
import com.Hanu.QrBasedAttendanceSystem.repo.AttendanceRepository;
import com.Hanu.QrBasedAttendanceSystem.repo.StudentQrRepository;
import com.Hanu.QrBasedAttendanceSystem.service.AttendanceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AttendanceServiceImp implements AttendanceService {

    private final StudentQrRepository studentQrRepository;
    private final AttendanceRepository attendanceRepository;

    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request) {

        User user = getUser();

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Faculty faculty = user.getFaculty();

        if(faculty == null) {
            throw new BadCredentialsException("Role must be faculty");
        }

        StudentQr studentQr = studentQrRepository.findByQrToken(request.getQrToken())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student qr not found")
                );

        if(studentQr.getStatus().equals(Status.INACTIVE)) {
            throw new ResourceNotAvailableException("Student qr is not available");
        }

        Student student = studentQr.getStudent();

        if(student.getStatus().equals(Status.INACTIVE)) {
            throw new ResourceNotAvailableException("Student not eligible");
        }

        if(!student.getFaculty().getFacultyId().equals(faculty.getFacultyId())) {
            throw new RelationMismatchException("Student Faculty relation mismatch");
        }

        if(attendanceRepository.existsByStudentAndDate(student, LocalDate.now())) {
            throw new RelationMismatchException("student already marked by faculty");
        }

        Attendance attendance = Attendance.builder()
                .student(student)
                .faculty(faculty)
                .date(LocalDate.now())
                .time(LocalDateTime.now())
                .status(AttendStatus.PRESENT)
                .build();

        attendance = attendanceRepository.save(attendance);

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentName(student.getUser().getName())
                .studentRoll(student.getRoll())
                .facultyId(faculty.getFacultyId())
                .attendanceDate(attendance.getDate())
                .attendanceTime(attendance.getTime())
                .status(attendance.getStatus())
                .build();
    }

    @Override
    public List<AttendanceResponse> getStudentAttendance() {

        User user = getUser();

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Student student = user.getStudent();

        if(student == null) {
            throw new BadCredentialsException("Your role must student");
        }

        List<Attendance> attendances = attendanceRepository.findByStudent(student);

        List<AttendanceResponse> responses = new ArrayList<>();

        for(Attendance attendance : attendances) {
            responses.add(mapToAttendanceResponse(attendance));
        }

        return responses;
    }

    @Override
    public List<AttendanceResponse> getFacultyAttendance() {

        User user = getUser();

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Faculty faculty = user.getFaculty();

        if(faculty == null) {
            throw new BadCredentialsException("Role must be faculty");
        }

        List<Attendance> attendances = attendanceRepository.findByFaculty(faculty);

        List<AttendanceResponse> responses = new ArrayList<>();

        for(Attendance attendance : attendances) {
            responses.add(mapToAttendanceResponse(attendance));
        }

        return responses;
    }

    // ======================= Helper function ======================

    public static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new BadCredentialsException("User not authenticated");
        }

        return (User) authentication.getPrincipal();
    }

    public static AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentName(attendance.getStudent().getUser().getName())
                .studentRoll(attendance.getStudent().getRoll())
                .facultyId(attendance.getFaculty().getFacultyId())
                .attendanceTime(attendance.getTime())
                .attendanceDate(attendance.getDate())
                .status(attendance.getStatus())
                .build();
    }


}
