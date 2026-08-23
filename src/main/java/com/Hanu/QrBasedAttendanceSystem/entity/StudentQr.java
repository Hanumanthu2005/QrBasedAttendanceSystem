package com.Hanu.QrBasedAttendanceSystem.entity;

import com.Hanu.QrBasedAttendanceSystem.entity.utils.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StudentQr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String qrToken;

    @OneToOne
    @JoinColumn(name = "student_id", unique = true)
    private Student student;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private String imagePath;
}
