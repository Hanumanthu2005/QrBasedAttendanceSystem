package com.Hanu.QrBasedAttendanceSystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class SecurityTestController {

    @GetMapping("/admin")
    public String adminTest() {
        return "Admin endpoint";
    }

    @GetMapping("/faculty")
    public String facultyTest() {
        return "Faculty endpoint";
    }

    @GetMapping("/student")
    public String studentTest() {
        return "Student endpoint";
    }
}
