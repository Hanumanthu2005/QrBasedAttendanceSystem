package com.Hanu.QrBasedAttendanceSystem.repo;

import com.Hanu.QrBasedAttendanceSystem.dto.attendanceReports.AttendanceReportProjection;
import com.Hanu.QrBasedAttendanceSystem.entity.Attendance;
import com.Hanu.QrBasedAttendanceSystem.entity.AttendanceSession;
import com.Hanu.QrBasedAttendanceSystem.entity.Faculty;
import com.Hanu.QrBasedAttendanceSystem.entity.Student;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.AttendStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudentAndSession(Student student, AttendanceSession session);

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByFaculty(Faculty faculty);

    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByFacultyAndDateBetween(Faculty faculty, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);

    List<Attendance> findBySession(AttendanceSession session);

    long countByFaculty(Faculty faculty);

    long countByFacultyAndDateBetween(Faculty faculty, LocalDate startDate, LocalDate endDate);

    long countByStudentAndStatus(Student student, AttendStatus status);

    long countByStudentAndStatusAndDateBetween(Student student, AttendStatus status, LocalDate startDate, LocalDate endDate);


    Optional<Attendance> findByStudentAndSession(
            Student student,
            AttendanceSession session
    );

    @Query("""
        SELECT
            s.id AS studentId,
            u.name AS studentName,
            s.roll AS studentRoll,
            a.id AS attendanceId,
            a.time AS attendanceTime,
            a.status AS status
        FROM Student s
        JOIN s.user u
        LEFT JOIN Attendance a
            ON a.student = s
            AND a.session = :session
        WHERE s.faculty = :faculty
        """)
    List<AttendanceReportProjection> findSessionAttendanceReport(
            @Param("faculty") Faculty faculty,
            @Param("session") AttendanceSession session
    );
}
