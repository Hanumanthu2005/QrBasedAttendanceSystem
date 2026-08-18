package com.Hanu.QrBasedAttendanceSystem.service.implementation;

import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceAlreadyExistException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotAvailableException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.student.StudentResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.student.UpdateStudentRequest;
import com.Hanu.QrBasedAttendanceSystem.entity.*;
import com.Hanu.QrBasedAttendanceSystem.repo.FacultyRepository;
import com.Hanu.QrBasedAttendanceSystem.repo.StudentRepository;
import com.Hanu.QrBasedAttendanceSystem.repo.UserRepository;
import com.Hanu.QrBasedAttendanceSystem.service.StudentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistException("User already exists with email " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .createdAt(LocalDateTime.now())
                .build();


        Faculty faculty = facultyRepository.findByFacultyId(request.getFacultyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found with id " + request.getFacultyId()))
                ;


        if(studentRepository.existsByRoll(request.getRoll())) {
            throw new ResourceAlreadyExistException("Student already exist with roll " + request.getRoll());
        }

        if(faculty.getStatus().equals(Status.INACTIVE)) {
            throw new ResourceNotAvailableException("Faculty is inactive try to assign with another faculty");
        }

        user = userRepository.save(user);


        Student student = Student.builder()
                .roll(request.getRoll())
                .user(user)
                .status(Status.ACTIVE)
                .faculty(faculty)
                .build();

        student = studentRepository.save(student);

        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getUser().getName())
                .roll(student.getRoll())
                .email(student.getUser().getEmail())
                .facultyId(student.getFaculty().getFacultyId())
                .status(student.getStatus().name())
                .build();
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        List<Student> students = studentRepository.findAll();

        List<StudentResponse> responses = new ArrayList<>();

        for(Student student : students) {
            StudentResponse temp = StudentResponse.builder()
                    .id(student.getId())
                    .name(student.getUser().getName())
                    .roll(student.getRoll())
                    .email(student.getUser().getEmail())
                    .facultyId(student.getFaculty().getFacultyId())
                    .status(student.getStatus().name())
                    .build();
            responses.add(temp);
        }
        return responses;
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id " + id)
                );

        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getUser().getName())
                .email(student.getUser().getEmail())
                .roll(student.getRoll())
                .facultyId(student.getFaculty().getFacultyId())
                .status(student.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
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

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUpdatedAt(LocalDateTime.now());

        Student existStudent = studentRepository.findByRoll(request.getRoll())
                .orElse(null);

        if(existStudent != null && !existStudent.getId().equals(student.getId())) {
            throw new ResourceAlreadyExistException("Student Already Exist with roll " + request.getRoll());
        }

        Faculty faculty = facultyRepository.findByFacultyId(request.getFacultyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not fount with id "+ request.getFacultyId())
                );

        if(faculty.getStatus().equals(Status.INACTIVE)) {
            throw new ResourceNotAvailableException("Faculty not ACTIVE try to assign another faculty");
        }

        user = userRepository.save(user);

        student.setFaculty(faculty);
        student.setRoll(request.getRoll());
        student.setUser(user);

        student = studentRepository.save(student);

        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getUser().getName())
                .email(student.getUser().getEmail())
                .roll(student.getRoll())
                .facultyId(student.getFaculty().getFacultyId())
                .status(student.getStatus().name())
                .build();
    }

    @Override
    public StudentResponse deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found with id " + id)
                );

        student.setStatus(Status.INACTIVE);

        student = studentRepository.save(student);

        return StudentResponse.builder()
                .id(student.getId())
                .name(student.getUser().getName())
                .email(student.getUser().getEmail())
                .roll(student.getRoll())
                .facultyId(student.getFaculty().getFacultyId())
                .status(student.getStatus().name())
                .build();
    }
}
