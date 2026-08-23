package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceAlreadyExistException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotAvailableException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceSessionResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.AttendanceSession;
import com.Hanu.QrBasedAttendanceSystem.entity.Faculty;
import com.Hanu.QrBasedAttendanceSystem.entity.User;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Role;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.SessionStatus;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Status;
import com.Hanu.QrBasedAttendanceSystem.repo.AttendanceSessionRepository;
import com.Hanu.QrBasedAttendanceSystem.service.AttendanceSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AttendanceSessionServiceImp implements AttendanceSessionService {

    private final AttendanceSessionRepository attendanceSessionRepository;

    @Override
    @Transactional
    public AttendanceSessionResponse createSession() {

        User user = getUser();

        if(user.getRole() != Role.FACULTY) {
            throw new BadCredentialsException("Role must be faculty");
        }

        Faculty faculty = user.getFaculty();

        if(faculty == null) {
            throw new ResourceNotFoundException("Faculty Not found");
        }

        if(faculty.getStatus() == Status.INACTIVE) {
            throw new ResourceNotAvailableException("Faculty Not available");
        }

        if(attendanceSessionRepository.findByFacultyAndStatus(faculty, SessionStatus.ACTIVE).isPresent()) {
            throw new ResourceAlreadyExistException("Session already created by this faculty");
        }

        if(attendanceSessionRepository.existsByFacultyAndDate(faculty, LocalDate.now())) {
            throw new ResourceAlreadyExistException("Today's Session already created");
        }

        AttendanceSession attendanceSession = mapToAttendanceSession(faculty);

        attendanceSession = attendanceSessionRepository.save(attendanceSession);

        return mapToAttendanceSessionResponse(attendanceSession);
    }


    // ===================== HELPER METHODS =====================

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new BadCredentialsException("user not authenticated");
        }

        return (User) authentication.getPrincipal();
    }

    private AttendanceSession mapToAttendanceSession(Faculty faculty) {
        LocalDateTime startTime = LocalDateTime.now();
        return AttendanceSession.builder()
                .faculty(faculty)
                .date(LocalDate.now())
                .startTime(startTime)
                .endTime(startTime.plusHours(2))
                .status(SessionStatus.ACTIVE)
                .build();
    }

    private AttendanceSessionResponse mapToAttendanceSessionResponse(AttendanceSession attendanceSession) {
        return AttendanceSessionResponse.builder()
                .id(attendanceSession.getId())
                .facultyId(attendanceSession.getFaculty().getFacultyId())
                .date(attendanceSession.getDate())
                .startTime(attendanceSession.getStartTime())
                .endTime(attendanceSession.getEndTime())
                .status(attendanceSession.getStatus())
                .build();
    }
}
