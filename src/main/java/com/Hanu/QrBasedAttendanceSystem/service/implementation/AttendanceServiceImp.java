package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.*;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.attendance.AttendanceResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports.AttendanceReportProjection;
import com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports.FacultyAttendanceSummary;
import com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports.StudentAttendanceSummary;
import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceReportResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.*;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.AttendStatus;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Role;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.SessionStatus;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Status;
import com.Hanu.QrBasedAttendanceSystem.repo.*;
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
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class AttendanceServiceImp implements AttendanceService {

    private final StudentQrRepository studentQrRepository;
    private final AttendanceRepository attendanceRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;


    //================= ADMIN ====================

    @Override
    public List<AttendanceResponse> getAllAttendance() {
        User user = getUser();

        validateAdmin(user);

        List<Attendance> attendances = attendanceRepository.findAll();

        return mapToAttendanceResponses(attendances);
    }

    @Override
    public List<AttendanceResponse> getAllAttendanceWithDateRange(LocalDate startDate, LocalDate endDate) {

        validateDates(startDate, endDate);

        User user = getUser();

        validateAdmin(user);

        List<Attendance> attendances = attendanceRepository.findByDateBetween(startDate, endDate);

        return mapToAttendanceResponses(attendances);
    }

    @Override
    public List<AttendanceResponse> getFacultyAttendance(Long id) {
        User user = getUser();

        validateAdmin(user);

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found")
                );

        List<Attendance> attendances = attendanceRepository.findByFaculty(faculty);

        return mapToAttendanceResponses(attendances);
    }

    @Override
    public List<AttendanceResponse> getStudentAttendance(Long id) {
        User user = getUser();

        validateAdmin(user);

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found")
                );

        List<Attendance> attendances = attendanceRepository.findByStudent(student);

        return mapToAttendanceResponses(attendances);
    }

    public List<AttendanceResponse> getFacultyAttendanceInBetween(Long id, LocalDate startDate, LocalDate endDate) {
        User user = getUser();

        validateDates(startDate, endDate);

        validateAdmin(user);

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found")
                );

        List<Attendance> attendances = attendanceRepository.findByFacultyAndDateBetween(faculty, startDate, endDate);

        return mapToAttendanceResponses(attendances);
    }

    // ====================== STUDENT ==========================

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
    public List<AttendanceResponse> getStudentAttendanceInBetween(LocalDate startDate, LocalDate endDate) {

        validateDates(startDate, endDate);

        User user = getUser();

        Student student = user.getStudent();

        if(student == null) {
            throw new BadCredentialsException("Role must be student");
        }

        List<Attendance> attendances = attendanceRepository.findByStudentAndDateBetween(student, startDate, endDate);

        return mapToAttendanceResponses(attendances);
    }

    @Override
    public StudentAttendanceSummary getStudentAttendanceSummary() {

        User user = getUser();

        Student student = user.getStudent();

        if(student == null) {
            throw new BadCredentialsException("Role must be student");
        }

        return getStudentAttendanceSummaryHelper(user, student);
    }

    @Override
    public StudentAttendanceSummary getStudentAttendanceSummary(LocalDate startDate, LocalDate endDate) {

        validateDates(startDate, endDate);

        User user = getUser();

        Student student = user.getStudent();

        if(student == null) {
            throw new BadCredentialsException("Role must be student");
        }

        return getStudentAttendanceSummaryHelper(user, student, startDate, endDate);
    }

    // ================= FACULTY =================


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

        AttendanceSession session = attendanceSessionRepository.findByFacultyAndStatus(faculty, SessionStatus.ACTIVE)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session Not found")
                );

        if(LocalDateTime.now().isAfter(session.getEndTime())) {
            throw new ResourceNotAvailableException("Session Closed");
        }

        if(attendanceRepository.existsByStudentAndSession(student, session)) {
            throw new ResourceAlreadyExistException("Student already attended the Session");
        }

        Attendance attendance = Attendance.builder()
                .session(session)
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


    @Override
    public List<AttendanceResponse> getFacultyAttendanceInBetween(LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        User user = getUser();

        Faculty faculty = user.getFaculty();

        if(faculty == null) {
            throw new BadCredentialsException("Role must be faculty");
        }

        List<Attendance> attendances = attendanceRepository.findByFacultyAndDateBetween(faculty, startDate, endDate);

        return mapToAttendanceResponses(attendances);
    }

    @Override
    @Transactional
    public FacultyAttendanceSummary getFacultyAttendanceSummary() {

        User user = getUser();

        Faculty faculty = facultyRepository.findFacultyWithStudents(user.getFaculty().getId()).orElseThrow(() -> new ResourceNotFoundException("Faculty Not found"));

        if(faculty == null) {
            throw new BadCredentialsException("Role must be faculty");
        }

        long totalSessions = attendanceSessionRepository.countByFaculty(faculty);

        long totalAttendanceRecords = attendanceRepository.countByFaculty(faculty);

        List<Student> students = faculty.getStudents();

        List<StudentAttendanceSummary> summaries = new ArrayList<>();

        for(Student student : students) {
            summaries.add(getStudentAttendanceSummaryHelper(user, student));
        }

        return FacultyAttendanceSummary.builder()
                .facultyId(faculty.getFacultyId())
                .facultyName(user.getName())
                .totalSessions(totalSessions)
                .totalAttendanceRecords(totalAttendanceRecords)
                .studentAttendanceSummaries(summaries)
                .build();
    }

    @Transactional
    public FacultyAttendanceSummary getFacultyAttendanceSummary(LocalDate startDate, LocalDate endDate) {

        validateDates(startDate, endDate);

        User user = getUser();

        Faculty faculty = facultyRepository.findFacultyWithStudents(user.getFaculty().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty Not found")); 

        if(faculty == null) {
            throw new BadCredentialsException("Role must be faculty");
        }

        long totalSessions = attendanceSessionRepository.countByFacultyAndDateBetween(faculty, startDate, endDate);

        long totalAttendanceRecords = attendanceRepository.countByFacultyAndDateBetween(faculty, startDate, endDate);

        List<Student> students = faculty.getStudents();

        List<StudentAttendanceSummary> summaries = new ArrayList<>();

        for(Student student : students) {
            summaries.add(getStudentAttendanceSummaryHelper(user, student, startDate, endDate));
        }

        return FacultyAttendanceSummary.builder()
                .facultyId(faculty.getFacultyId())
                .facultyName(user.getName())
                .totalSessions(totalSessions)
                .totalAttendanceRecords(totalAttendanceRecords)
                .studentAttendanceSummaries(summaries)
                .build();
    }

    @Override
    @Transactional
    public AttendanceReportResponse getSessionAttendances(Long sessionId) {

        User user = getUser();

        Faculty faculty = user.getFaculty();

        if (faculty == null) {
            throw new BadCredentialsException("Role must be faculty");
        }

        AttendanceSession session = attendanceSessionRepository
                .findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found")
                );

        validateSessionOwnership(faculty, session);

        List<AttendanceReportProjection> records =
                attendanceRepository.findSessionAttendanceReport(
                        faculty,
                        session
                );

        List<AttendanceResponse> responses = records.stream()
                .map(record -> AttendanceResponse.builder()
                        .id(record.getAttendanceId())
                        .studentName(record.getStudentName())
                        .studentRoll(record.getStudentRoll())
                        .facultyId(faculty.getFacultyId())
                        .attendanceDate(session.getDate())
                        .attendanceTime(record.getAttendanceTime())
                        .status(
                                record.getAttendanceId() == null
                                        ? AttendStatus.ABSENT
                                        : record.getStatus()
                        )
                        .sessionId(session.getId())
                        .build()
                )
                .toList();

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


    // ======================= Helper function ======================

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

    private static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new BadCredentialsException("User not authenticated");
        }

        return (User) authentication.getPrincipal();
    }

    private static AttendanceResponse mapToAttendanceResponse(Attendance attendance) {
        return AttendanceResponse.builder()
                .id(attendance.getId())
                .studentName(attendance.getStudent().getUser().getName())
                .studentRoll(attendance.getStudent().getRoll())
                .facultyId(attendance.getFaculty().getFacultyId())
                .attendanceTime(attendance.getTime())
                .attendanceDate(attendance.getDate())
                .status(attendance.getStatus())
                .sessionId(attendance.getSession().getId())
                .build();
    }

    private List<AttendanceResponse> mapToAttendanceResponses(List<Attendance> attendances) {
        List<AttendanceResponse> responses = new ArrayList<>();

        for(Attendance attendance : attendances) {
            responses.add(mapToAttendanceResponse(attendance));
        }

        return responses;
    }

    private void validateAdmin(User user) {
        if(!user.getRole().equals(Role.ADMIN)) {
            throw new BadCredentialsException("Role must be admin");
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if(startDate == null || endDate == null) {
            throw new BadInputException("Both dates need to be provided");
        }

        if(startDate.isAfter(endDate)) {
            throw new BadInputException("Start date must be before end date or same date");
        }
    }

    private StudentAttendanceSummary getStudentAttendanceSummaryHelper(User user, Student student) {
        Faculty faculty = student.getFaculty();

        if(faculty == null) {
            throw  new ResourceNotFoundException("Faculty Not found");
        }

        long totalSessions = attendanceSessionRepository.countByFaculty(faculty);

        long presentSession = attendanceRepository.countByStudentAndStatus(student, AttendStatus.PRESENT);

        long absentSession = totalSessions - presentSession;

        double attendancePercentage = 0;

        if(totalSessions != 0) {
            attendancePercentage = ((double) presentSession / totalSessions) * 100;
        }

        return StudentAttendanceSummary.builder()
                .name(user.getName())
                .roll(student.getRoll())
                .totalSessions(totalSessions)
                .presentSessions(presentSession)
                .absentSessions(absentSession)
                .attendancePercentage(attendancePercentage)
                .build();
    }

    private StudentAttendanceSummary getStudentAttendanceSummaryHelper(User user, Student student, LocalDate startDate, LocalDate endDate){
        Faculty faculty = student.getFaculty();

        if(faculty == null) {
            throw  new ResourceNotFoundException("Faculty Not found");
        }

        long totalSessions = attendanceSessionRepository.countByFacultyAndDateBetween(faculty, startDate, endDate);

        long presentSession = attendanceRepository.countByStudentAndStatusAndDateBetween(student, AttendStatus.PRESENT, startDate, endDate);

        long absentSession = totalSessions - presentSession;

        double attendancePercentage = 0.0;

        if(totalSessions != 0) {
            attendancePercentage = ((double) presentSession / totalSessions) * 100;
        }

        return StudentAttendanceSummary.builder()
                .name(student.getUser().getName())
                .roll(student.getRoll())
                .totalSessions(totalSessions)
                .presentSessions(presentSession)
                .absentSessions(absentSession)
                .attendancePercentage(attendancePercentage)
                .build();
    }
}
