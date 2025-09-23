package ir.lms.controller;

import ir.lms.service.StudentService;
import ir.lms.dto.ApiResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/take/course/{courseId}")
    public ResponseEntity<ApiResponseDTO> studentGetCourse(@PathVariable Long courseId , Principal principal) {
        studentService.studentTakeCourse(courseId , principal);
        return ResponseEntity.ok(new ApiResponseDTO("Course get success." , true));
    }
}
