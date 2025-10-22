package ir.lms.controller;


import ir.lms.util.dto.ApiResponse;
import ir.lms.util.dto.mapper.CourseMapper;
import ir.lms.model.Course;
import ir.lms.service.CourseService;
import ir.lms.util.dto.CourseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
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

    private static final String ADMIN_OR_MANAGER = "hasRole('ADMIN') OR hasRole('MANAGER')";
    private static final String ALL_AUTHENTICATED = "hasRole('ADMIN') OR hasRole('MANAGER') OR hasRole('STUDENT')";



    @PreAuthorize(ADMIN_OR_MANAGER)
    @PostMapping
    public ResponseEntity<CourseDTO> save(@Valid @RequestBody CourseDTO dto) {
        Course course = courseService.persist(courseMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(courseMapper.toDto(course));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        Course course = courseService.update(id , courseMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(courseMapper.toDto(course));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> findById(@PathVariable Long id) {
        Course course = courseService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(courseMapper.toDto(course));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping
    public ResponseEntity<List<CourseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                courseService.findAll().stream().map(courseMapper::toDto).toList()
        );
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/major-courses")
    public ResponseEntity<List<CourseDTO>> findAllMajorCourses(@Valid @RequestParam String majorName) {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.findAllMajorCourses(majorName).stream()
                                .map(courseMapper::toDto)
                                .toList());
    }
}
