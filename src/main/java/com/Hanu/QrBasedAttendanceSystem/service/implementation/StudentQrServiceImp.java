package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.QrReadException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotAvailableException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Status;
import com.Hanu.QrBasedAttendanceSystem.entity.Student;
import com.Hanu.QrBasedAttendanceSystem.entity.StudentQr;
import com.Hanu.QrBasedAttendanceSystem.entity.User;
import com.Hanu.QrBasedAttendanceSystem.service.StudentQrService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class StudentQrServiceImp implements StudentQrService {
    
    @Override
    public byte[] getStudentQr() {

        User user = getUser();

        if(user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Student student = user.getStudent();

        if(student == null) {
            throw new BadCredentialsException("Invalid credentials");
        }

        StudentQr studentQr = student.getStudentQr();

        if(studentQr == null) {
            throw new ResourceNotFoundException("Student qr not available");
        }

        if(studentQr.getStatus().equals(Status.INACTIVE))
        {
            throw new ResourceNotAvailableException("Student qr not available");
        }

        String imagePath = studentQr.getImagePath();

        if(!Files.exists(Path.of(imagePath)))
        {
            throw new ResourceNotFoundException("Qr image not found");
        }

        return getQr(imagePath);

    }

    // ===================== HELPER METHODS ========================

    private User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            throw new BadCredentialsException("no valid user");
        }

        return (User) authentication.getPrincipal();
    }

    private byte[] getQr(String imagePath) {

        byte[] qr;

        try {
            qr = Files.readAllBytes(Path.of(imagePath));
        } catch (Exception e) {
            throw new QrReadException("Error occured while reading qr image file");
        }

        return qr;
    }
}
