package com.Hanu.QrBasedAttendanceSystem.service.implementation;


import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceAlreadyExistException;
import com.Hanu.QrBasedAttendanceSystem.Exception.ResourceNotFoundException;
import com.Hanu.QrBasedAttendanceSystem.dto.faculty.FacultyRequest;
import com.Hanu.QrBasedAttendanceSystem.dto.faculty.FacultyResponse;
import com.Hanu.QrBasedAttendanceSystem.dto.faculty.UpdateFacultyRequest;
import com.Hanu.QrBasedAttendanceSystem.entity.Faculty;
import com.Hanu.QrBasedAttendanceSystem.entity.Role;
import com.Hanu.QrBasedAttendanceSystem.entity.Status;
import com.Hanu.QrBasedAttendanceSystem.entity.User;
import com.Hanu.QrBasedAttendanceSystem.repo.FacultyRepository;
import com.Hanu.QrBasedAttendanceSystem.repo.UserRepository;
import com.Hanu.QrBasedAttendanceSystem.service.FacultyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacultyServiceImp implements FacultyService {

    private final UserRepository userRepository;
    private final FacultyRepository facultyRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public FacultyResponse createFaculty(FacultyRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistException("User already existed by  email" + request.getEmail());
        }

        User user = User.builder()
             .name(request.getName())
             .email(request.getEmail())
             .password(passwordEncoder.encode(request.getPassword()))
             .role(Role.FACULTY)
             .createdAt(LocalDateTime.now())
             .build();

        user = userRepository.save(user);

        if(facultyRepository.existsByFacultyId(request.getFacultyId())) {
            throw new ResourceAlreadyExistException("Faculty exist already with faculty id" + request.getFacultyId());
        }

        Faculty faculty = Faculty.builder()
                .facultyId(request.getFacultyId())
                .user(user)
                .status(Status.ACTIVE)
                .build();

        faculty = facultyRepository.save(faculty);

        return FacultyResponse.builder()
                .id(faculty.getId())
                .facultyId(faculty.getFacultyId())
                .name(faculty.getUser().getName())
                .email(faculty.getUser().getEmail())
                .status(faculty.getStatus().name())
                .build();
    }

    public List<FacultyResponse> getFaculty() {
        List<Faculty> faculties = facultyRepository.findAll();

        List<FacultyResponse> res = new ArrayList<>();

        for(Faculty faculty : faculties) {
            FacultyResponse temp = FacultyResponse.builder()
                    .id(faculty.getId())
                    .facultyId(faculty.getFacultyId())
                    .name(faculty.getUser().getName())
                    .email(faculty.getUser().getEmail())
                    .status(faculty.getStatus().name())
                    .build();

            res.add(temp);
        }

        return res;
    }

    public FacultyResponse getFacultyById(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("faculty not found with id" + id)
                );

        return FacultyResponse.builder()
                .id(faculty.getId())
                .name(faculty.getUser().getName())
                .facultyId(faculty.getFacultyId())
                .email(faculty.getUser().getEmail())
                .status(faculty.getStatus().name())
                .build();
    }

    @Transactional
    public FacultyResponse updateFaculty(Long id, UpdateFacultyRequest request) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty not found with id" + id)
                );

        User user = faculty.getUser();

        User existingUser = userRepository.findByEmail(request.getEmail())
                        .orElse(null);

        if(existingUser != null && !existingUser.getId().equals(user.getId()))
            throw new ResourceAlreadyExistException("Email already belongs to the another user");

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);

        Faculty exists = facultyRepository.findByFacultyId(request.getFacultyId())
                        .orElse(null);

        if(exists != null && !exists.getId().equals(faculty.getId()))
            throw new ResourceAlreadyExistException("Faculty already exist with id " + request.getFacultyId());


        faculty.setFacultyId(request.getFacultyId());
        faculty.setUser(user);

        facultyRepository.save(faculty);

        return FacultyResponse.builder()
                .id(faculty.getId())
                .name(user.getName())
                .facultyId(faculty.getFacultyId())
                .email(faculty.getUser().getEmail())
                .status(faculty.getStatus().name())
                .build();

    }

    public FacultyResponse deleteFaculty(Long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Faculty with id not found")
                );

        faculty.setStatus(Status.INACTIVE);

        facultyRepository.save(faculty);

        return FacultyResponse.builder()
                .id(faculty.getId())
                .name(faculty.getUser().getName())
                .facultyId(faculty.getFacultyId())
                .email(faculty.getUser().getEmail())
                .status(faculty.getStatus().name())
                .build();
    }

}
