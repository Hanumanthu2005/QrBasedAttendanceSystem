package com.Hanu.QrBasedAttendanceSystem.repo;

import com.Hanu.QrBasedAttendanceSystem.entity.AttendanceSession;
import com.Hanu.QrBasedAttendanceSystem.entity.Faculty;
import com.Hanu.QrBasedAttendanceSystem.entity.Student;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {

    Optional<AttendanceSession> findByFacultyAndStatus(Faculty faculty, SessionStatus status);

    List<AttendanceSession> findByStatusAndEndTimeLessThanEqual(SessionStatus status, LocalDateTime time);

    boolean existsByFacultyAndDate(Faculty faculty, LocalDate now);
}
