package com.Hanu.QrBasedAttendanceSystem.repo;

import com.Hanu.QrBasedAttendanceSystem.entity.Student;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRoll(String roll);

    boolean existsByRoll(@NotBlank(message = "Roll no is required") String roll);
}
