package ir.lms.controller;

import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.mapper.CourseMapper;
import ir.lms.model.Course;
import ir.lms.service.CourseService;
import ir.lms.util.dto.course.CourseDTO;
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
    public ResponseEntity<CourseDTO> save(@RequestBody CourseDTO dto) {
        Course course = courseService.persist(courseMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseMapper.toDto(courseService.persist(course)));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> update(@PathVariable Long id, @RequestBody CourseDTO dto) {
        Course course = courseService.persist(courseMapper.toEntity(dto));
        course.setId(id);
        return ResponseEntity.ok(courseMapper.toDto(course));
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
}
