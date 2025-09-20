package ir.lms.controller;

import ir.lms.service.CourseService;
import ir.lms.service.OfferedCourseService;
import ir.lms.service.StudentService;
import ir.lms.util.dto.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/get/course/{courseId}")
    public ResponseEntity<ApiResponseDTO> studentGetCourse(@PathVariable Long courseId , Principal principal) {
        studentService.studentGetCourse(courseId , principal);
        return ResponseEntity.ok(new ApiResponseDTO("Course get success." , true));
    }
}
