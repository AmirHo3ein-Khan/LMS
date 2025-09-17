package ir.lms.controller;

import ir.lms.service.CourseService;
import ir.lms.service.OfferedCourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final OfferedCourseService offeredCourseService;

    public StudentController(OfferedCourseService offeredCourseService) {
        this.offeredCourseService = offeredCourseService;
    }

//    public ResponseEntity<List<OfferedCourse>> findAllOfferedCourses() {
//
//    }
}
