package com.Hanu.QrBasedAttendanceSystem.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceRequest {

    @NotBlank(message = "Qr is required for attendance")
    private String qrToken;

}
