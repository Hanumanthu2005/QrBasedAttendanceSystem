package com.Hanu.QrBasedAttendanceSystem.service;

import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceReportResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.session.AttendanceSessionResponse;
import com.Hanu.QrBasedAttendanceSystem.entity.AttendanceSession;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface AttendanceSessionService {

    AttendanceSessionResponse createSession();

    AttendanceSessionResponse getActiveSession();

    AttendanceSessionResponse closeSession(Long id);

    List<AttendanceSessionResponse> getAllSession();

    AttendanceReportResponse getAdminSessionAttendance(Long sessionId);
}
