package com.Hanu.QrBasedAttendanceSystem.repo;

import com.Hanu.QrBasedAttendanceSystem.entity.Faculty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    boolean existsByFacultyId(String id);

    Optional<Faculty> findByFacultyId(String id);

    @Query("""
        SELECT DISTINCT f
        FROM Faculty f
        LEFT JOIN FETCH f.students
        WHERE f.id = :facultyId
    """)
    Optional<Faculty> findFacultyWithStudents(@Param("facultyId") Long facultyId);
}
