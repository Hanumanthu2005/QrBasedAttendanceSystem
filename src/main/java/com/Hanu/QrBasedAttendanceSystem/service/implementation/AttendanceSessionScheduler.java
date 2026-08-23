package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.entity.AttendanceSession;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.SessionStatus;
import com.Hanu.QrBasedAttendanceSystem.repo.AttendanceSessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceSessionScheduler {

    private final AttendanceSessionRepository attendanceSessionRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireAttendanceSessions() {

        LocalDateTime now = LocalDateTime.now();

        List<AttendanceSession> sessions = attendanceSessionRepository
                .findByStatusAndEndTimeLessThanEqual(SessionStatus.ACTIVE, now);

        for(AttendanceSession attendanceSession : sessions) {
            attendanceSession.setStatus(SessionStatus.CLOSED);
        }

        attendanceSessionRepository.saveAll(sessions);

    }
}
