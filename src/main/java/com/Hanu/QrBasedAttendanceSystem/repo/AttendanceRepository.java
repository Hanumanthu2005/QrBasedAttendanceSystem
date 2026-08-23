package com.Hanu.QrBasedAttendanceSystem.repo;

import com.Hanu.QrBasedAttendanceSystem.entity.Attendance;
import com.Hanu.QrBasedAttendanceSystem.entity.AttendanceSession;
import com.Hanu.QrBasedAttendanceSystem.entity.Faculty;
import com.Hanu.QrBasedAttendanceSystem.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudentAndSession(Student student, AttendanceSession session);

    List<Attendance> findByStudent(Student student);

    List<Attendance> findByFaculty(Faculty faculty);

    List<Attendance> findByStudentAndDateBetween(Student student, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByFacultyAndDateBetween(Faculty faculty, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
