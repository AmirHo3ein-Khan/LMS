package ir.lms.controller;

import ir.lms.dto.ApiResponseDTO;
import ir.lms.mapper.CourseMapper;
import ir.lms.model.Course;
import ir.lms.service.CourseService;
import ir.lms.dto.course.CourseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CourseDTO> save(@Valid @RequestBody CourseDTO dto) {
        Course course = courseService.persist(courseMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseMapper.toDto(course));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> update(@PathVariable Long id,@Valid @RequestBody CourseDTO dto) {
        Course updated = courseService.update(id, courseMapper.toEntity(dto));
        return ResponseEntity.ok(courseMapper.toDto(updated));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Course deleted success." , true));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseMapper.toDto(courseService.findById(id)));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<CourseDTO>> findAll() {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        for (Course course : courseService.findAll()) courseDTOS.add(courseMapper.toDto(course));
        return ResponseEntity.ok(courseDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/major/courses")
    public ResponseEntity<List<CourseDTO>> findAllMajorCourses(@Valid @RequestBody String majorName) {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        for (Course course : courseService.findAllMajorCourses(majorName)) courseDTOS.add(courseMapper.toDto(course));
        return ResponseEntity.ok(courseDTOS);
    }
}
