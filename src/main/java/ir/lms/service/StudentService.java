package ir.lms.service;

import ir.lms.util.dto.ApiResponseDTO;

import java.security.Principal;

public interface StudentService {
    void studentGetCourse(Long courseId , Principal principal);
}
