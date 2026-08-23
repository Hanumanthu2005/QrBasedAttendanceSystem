package com.Hanu.QrBasedAttendanceSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class QrBasedAttendanceSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(QrBasedAttendanceSystemApplication.class, args);
	}

}
