package com.Hanu.QrBasedAttendanceSystem.entity;

import com.Hanu.QrBasedAttendanceSystem.entity.utils.Status;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String facultyId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @OneToMany(mappedBy = "faculty")
    private List<Student> students;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "faculty")
    private List<Attendance> attendance;

    @OneToMany(mappedBy = "faculty")
    private List<AttendanceSession> sessions;
}
