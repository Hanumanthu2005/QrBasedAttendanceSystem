package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.*;
import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.student.UpdateStudentRequest;
import com.Hanu.QrBasedAttendanceSystem.entity.*;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Role;
import com.Hanu.QrBasedAttendanceSystem.entity.utils.Status;
import com.Hanu.QrBasedAttendanceSystem.repo.FacultyRepository;
import com.Hanu.QrBasedAttendanceSystem.repo.StudentQrRepository;
import com.Hanu.QrBasedAttendanceSystem.repo.StudentRepository;
import com.Hanu.QrBasedAttendanceSystem.repo.UserRepository;
import com.Hanu.QrBasedAttendanceSystem.security.QrTokenGenerator;
import com.Hanu.QrBasedAttendanceSystem.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImp implements StudentService {

    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final StudentQrRepository studentQrRepository;
    private final QrImageGenerator qrImageGenerator;
    private final QrFileStorageService qrFileStorageService;


    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistException("User already exists with email " + request.getEmail());
        }

        if (studentRepository.existsByRoll(request.getRoll())) {
            throw new ResourceAlreadyExistException(
                    "Student already exists with roll " + request.getRoll()
            );
        }

        Faculty faculty = facultyRepository.findByFacultyId(request.getFacultyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found with id " + request.getFacultyId()))
                ;

        validateFaculty(faculty);

        User user = getUser(request);

        user = userRepository.save(user);

        Student student = Student.builder()
                .roll(request.getRoll())
                .user(user)
                .status(Status.ACTIVE)
                .faculty(faculty)
                .build();

        student = studentRepository.save(student);

        String qrToken = getQrToken();

        String fileName = getFileName(qrToken, student);

        StudentQr studentQr = mapToStudentQr(
                student,
                qrToken,
                fileName
        );

        student.setStudentQr(studentQr);

        studentQrRepository.save(studentQr);

        return mapToStudentResponse(student);
    }

    @Override
    public List<StudentResponse> getAllStudents() {

        List<Student> students = studentRepository.findAll();

        return mapToStudentResponses(students);
    }

    @Override
    public StudentResponse getStudentById(Long id) {

        if (id == null || id <= 0) {
            throw new BadInputException("Invalid student id");
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id " + id)
                );

        return mapToStudentResponse(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {

        if (id == null || id <= 0) {
            throw new BadInputException("Invalid student id");
        }

        if (request == null) {
            throw new BadInputException(
                    "Student update request cannot be null"
            );
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id " + id)
                );

        User user = student.getUser();

        User existsUser = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if(existsUser != null && !existsUser.getId().equals(user.getId())) {
            throw new ResourceAlreadyExistException("Another user already exist with the email" + request.getEmail());
        }

        Student existStudent = studentRepository.findByRoll(request.getRoll())
                .orElse(null);

        if(existStudent != null && !existStudent.getId().equals(student.getId())) {
            throw new ResourceAlreadyExistException("Student Already Exist with roll " + request.getRoll());
        }

        Faculty faculty = facultyRepository.findByFacultyId(request.getFacultyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not fount with id "+ request.getFacultyId())
                );

        validateFaculty(faculty);

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        student.setFaculty(faculty);
        student.setRoll(request.getRoll());
        student.setUser(user);

        student = studentRepository.save(student);

        return mapToStudentResponse(student);
    }

    @Override
    public StudentResponse deleteStudent(Long id) {

        if (id == null || id <= 0) {
            throw new BadInputException("Invalid student id");
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id " + id)
                );

        if (student.getStatus() == Status.INACTIVE) {
            throw new ResourceNotAvailableException(
                    "Student is already inactive"
            );
        }

        student.setStatus(Status.INACTIVE);

        StudentQr studentQr = student.getStudentQr();

        if (studentQr != null) {
            studentQr.setStatus(Status.INACTIVE);
            studentQrRepository.save(studentQr);
        }

        student = studentRepository.save(student);

        return mapToStudentResponse(student);
    }


    // =================== HELPER METHODS ===================

    private User getUser(StudentRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private String getQrToken() {
        String qrToken;

        do {
            qrToken = QrTokenGenerator.generateToken();

        } while(studentQrRepository.existsByQrToken(qrToken));

        return qrToken;
    }

    private String getFileName(String qrToken, Student student) {

        BufferedImage image;

        try {

            image = qrImageGenerator.generate(qrToken);

        } catch (Exception e) {

            throw new QrGenerationException("Error occured while generating qr image " + e);

        }

        String fileName;

        try {

            fileName = qrFileStorageService.store(image, "student-" + student.getId() + ".png");

        } catch (IOException e) {

            throw new QrFileStorageException("Error occured while storing qr image " + e);

        }

        return fileName;
    }

    private StudentQr mapToStudentQr(Student student, String qrToken, String fileName) {
        return StudentQr.builder()
                .student(student)
                .qrToken(qrToken)
                .createdAt(LocalDateTime.now())
                .status(Status.ACTIVE)
                .imagePath(fileName)
                .build();
    }

    private StudentResponse mapToStudentResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getUser().getName())
                .roll(student.getRoll())
                .email(student.getUser().getEmail())
                .facultyId(student.getFaculty().getFacultyId())
                .status(student.getStatus().name())
                .build();
    }

    private List<StudentResponse> mapToStudentResponses(List<Student> students) {
        List<StudentResponse> responses = new ArrayList<>();

        for(Student student : students) {
            responses.add(mapToStudentResponse(student));
        }
        return responses;
    }

    private void validateFaculty(Faculty faculty) {

        if (faculty == null) {
            throw new ResourceNotFoundException(
                    "Faculty not found"
            );
        }

        if (faculty.getStatus() == Status.INACTIVE) {
            throw new ResourceNotAvailableException(
                    "Faculty is not active. Try assigning another faculty"
            );
        }
    }
}
