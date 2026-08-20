package com.Hanu.QrBasedAttendanceSystem.repo;

import com.Hanu.QrBasedAttendanceSystem.entity.Student;
import com.Hanu.QrBasedAttendanceSystem.entity.StudentQr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentQrRepository extends JpaRepository<StudentQr, Long> {

    Optional<StudentQr> findByQrToken(String qrToken);

    boolean existsByStudent(Student student);

    boolean existsByQrToken(String qrToken);

}
