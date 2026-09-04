package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.BadInputException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceAlreadyExistException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotAvailableException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports.AttendanceReportProjection;
import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceReportResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceSessionResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.*;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.AttendStatus;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Role;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.SessionStatus;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Status;
import com.Hanu.QrBasedAttendanceSystem.repo.AttendanceRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AttendanceSessionServiceImp implements AttendanceSessionService {

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceRepository attendanceRepository;

    //=========================
    // ADMIN
    //=========================

    @Override
    @Transactional
    public AttendanceReportResponse getAdminSessionAttendance(Long sessionId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new BadCredentialsException("User must be authenticated");
        }

        User user = (User) authentication.getPrincipal();

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (user.getRole() != Role.ADMIN) {
            throw new BadCredentialsException("Role must be admin");
        }

        if (sessionId == null || sessionId <= 0) {
            throw new BadInputException("Invalid session id");
        }

        AttendanceSession session =
                attendanceSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Session not found with id " + sessionId
                                )
                        );

        Faculty faculty = getAuthenticatedFaculty();

        List<AttendanceReportProjection> records =
                attendanceRepository.findSessionAttendanceReport(
                        faculty,
                        session
                );

        List<AttendanceResponse> responses = new ArrayList<>();

        for (AttendanceReportProjection record : records) {

            AttendStatus status;

            if (record.getAttendanceId() == null) {
                status = AttendStatus.ABSENT;
            } else {
                status = record.getStatus();
            }

            AttendanceResponse response = AttendanceResponse.builder()
                    .id(record.getAttendanceId())
                    .studentName(record.getStudentName())
                    .studentRoll(record.getStudentRoll())
                    .facultyId(faculty.getFacultyId())
                    .attendanceDate(
                            record.getAttendanceId() == null
                                    ? null
                                    : session.getDate()
                    )
                    .attendanceTime(record.getAttendanceTime())
                    .status(status)
                    .build();

            responses.add(response);
        }

        return AttendanceReportResponse.builder()
                .sessionId(session.getId())
                .facultyId(faculty.getFacultyId())
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .attendances(responses)
                .build();
    }


    @Override
    public List<AttendanceSessionResponse> getAllSession() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new BadCredentialsException(
                    "User not authenticated"
            );
        }

        User user = (User) authentication.getPrincipal();

        if (user == null) {
            throw new ResourceNotFoundException(
                    "User not found"
            );
        }

        if (user.getRole() != Role.ADMIN) {
            throw new BadCredentialsException(
                    "Only admin can perform this operation"
            );
        }

        List<AttendanceSession> sessions =
                attendanceSessionRepository.findAll();

        return mapToResponses(sessions);
    }


    //=========================
    // FACULTY
    //=========================

    @Override
    @Transactional
    public AttendanceSessionResponse createSession() {

        Faculty faculty = getAuthenticatedFaculty();

        if (faculty.getStatus() == Status.INACTIVE) {
            throw new ResourceNotAvailableException("Faculty not available");
        }

        if (hasActiveSession(faculty)) {
            throw new ResourceAlreadyExistException(
                    "Session already created by this faculty"
            );
        }

        if (attendanceSessionRepository.existsByFacultyAndDate(
                faculty,
                LocalDate.now()
        )) {
            throw new ResourceAlreadyExistException(
                    "Today's session already created"
            );
        }

        AttendanceSession session = buildSession(faculty);

        return mapToResponse(
                attendanceSessionRepository.save(session)
        );
    }

    @Override
    public AttendanceSessionResponse getActiveSession() {

        Faculty faculty = getAuthenticatedFaculty();

        AttendanceSession session = attendanceSessionRepository
                .findByFacultyAndStatus(faculty, SessionStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active attendance session is not available"
                        )
                );

        return mapToResponse(session);
    }

    @Override
    @Transactional
    public AttendanceSessionResponse closeSession(Long id) {

        Faculty faculty = getAuthenticatedFaculty();

        AttendanceSession session = attendanceSessionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found")
                );

        validateSessionOwnership(faculty, session);

        if (session.getStatus() == SessionStatus.CLOSED) {
            throw new BadInputException("Session is already closed");
        }

        session.setStatus(SessionStatus.CLOSED);

        return mapToResponse(session);
    }

    @Override
    public List<AttendanceSessionResponse> getFacultySession() {

        Faculty faculty = getAuthenticatedFaculty();

        List<AttendanceSession> sessions = attendanceSessionRepository.findByFaculty(faculty);

        return mapToResponses(sessions);
    }

    @Override
    @Transactional
    public AttendanceReportResponse getFacultySessionAttendance(Long sessionId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new BadCredentialsException("User must be authenticated");
        }

        User user = (User) authentication.getPrincipal();

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if (user.getRole() != Role.FACULTY) {
            throw new BadCredentialsException("Role must be admin");
        }

        if (sessionId == null || sessionId <= 0) {
            throw new BadInputException("Invalid session id");
        }

        AttendanceSession session =
                attendanceSessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Session not found with id " + sessionId
                                )
                        );

        Faculty faculty = user.getFaculty();

        if(faculty == null) {
            throw new ResourceNotFoundException("Faculty Not found");
        }

        List<AttendanceReportProjection> records =
                attendanceRepository.findSessionAttendanceReport(
                        faculty,
                        session
                );

        List<AttendanceResponse> responses = new ArrayList<>();

        for (AttendanceReportProjection record : records) {

            AttendStatus status;

            if (record.getAttendanceId() == null) {
                status = AttendStatus.ABSENT;
            } else {
                status = record.getStatus();
            }

            AttendanceResponse response = AttendanceResponse.builder()
                    .id(record.getAttendanceId())
                    .studentName(record.getStudentName())
                    .studentRoll(record.getStudentRoll())
                    .facultyId(faculty.getFacultyId())
                    .attendanceDate(
                            record.getAttendanceId() == null
                                    ? null
                                    : session.getDate()
                    )
                    .attendanceTime(record.getAttendanceTime())
                    .status(status)
                    .build();

            responses.add(response);
        }

        return AttendanceReportResponse.builder()
                .sessionId(session.getId())
                .facultyId(faculty.getFacultyId())
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .attendances(responses)
                .build();
    }

    // =========================
    // AUTHENTICATION
    // =========================

    private Faculty getAuthenticatedFaculty() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            throw new BadCredentialsException("User not authenticated");
        }

        User user = (User) authentication.getPrincipal();

        assert user != null;
        if (user.getRole() != Role.FACULTY) {
            throw new BadCredentialsException(
                    "Only faculty can perform this operation"
            );
        }

        Faculty faculty = user.getFaculty();

        if (faculty == null) {
            throw new ResourceNotFoundException("Faculty not found");
        }

        return faculty;
    }



    // =========================
    // VALIDATION
    // =========================

    private boolean hasActiveSession(Faculty faculty) {
        return attendanceSessionRepository
                .findByFacultyAndStatus(faculty, SessionStatus.ACTIVE)
                .isPresent();
    }

    private void validateSessionOwnership(
            Faculty faculty,
            AttendanceSession session
    ) {

        if (!Objects.equals(
                faculty.getId(),
                session.getFaculty().getId()
        )) {
            throw new BadCredentialsException(
                    "Faculty does not own this session"
            );
        }
    }

    // =========================
    // MAPPING
    // =========================

    private AttendanceSession buildSession(Faculty faculty) {

        LocalDateTime startTime = LocalDateTime.now();

        return AttendanceSession.builder()
                .faculty(faculty)
                .date(startTime.toLocalDate())
                .startTime(startTime)
                .endTime(startTime.plusHours(2))
                .status(SessionStatus.ACTIVE)
                .build();
    }

    private AttendanceSessionResponse mapToResponse(
            AttendanceSession session
    ) {

        return AttendanceSessionResponse.builder()
                .id(session.getId())
                .facultyId(session.getFaculty().getFacultyId())
                .date(session.getDate())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .build();
    }

    private List<AttendanceSessionResponse> mapToResponses(List<AttendanceSession> sessions) {

        List<AttendanceSessionResponse> responses = new ArrayList<>();

        for(AttendanceSession session : sessions) {
            responses.add(mapToResponse(session));
        }

        return responses;
    }
}