package ir.lms.controller;


import ir.lms.util.dto.ApiResponse;
import ir.lms.util.dto.ApiResponseDTO;
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
    public ResponseEntity<ApiResponse<CourseDTO>> save(@Valid @RequestBody CourseDTO dto) {
        Course course = courseService.persist(courseMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<CourseDTO>builder()
                        .success(true)
                        .message("course.creation.success")
                        .data(courseMapper.toDto(course))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> update(@PathVariable Long id, @Valid @RequestBody CourseDTO dto) {
        Course course = courseService.update(id , courseMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CourseDTO>builder()
                        .success(true)
                        .message("course.update.success")
                        .data(courseMapper.toDto(course))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO("course.deleted.success." , true));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseDTO>> findById(@PathVariable Long id) {
        Course course = courseService.findById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<CourseDTO>builder()
                        .success(true)
                        .message("course.get.success")
                        .data(courseMapper.toDto(course))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseDTO>>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<CourseDTO>>builder()
                        .success(true)
                        .message("courses.get.success")
                        .data(courseService.findAll().stream().map(courseMapper::toDto).toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/major-courses")
    public ResponseEntity<ApiResponse<List<CourseDTO>>> findAllMajorCourses(@Valid @RequestParam String majorName) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<CourseDTO>>builder()
                        .success(true)
                        .message("courses.get.success")
                        .data(courseService.findAllMajorCourses(majorName).stream()
                                .map(courseMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }
}
