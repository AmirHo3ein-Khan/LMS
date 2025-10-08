package ir.lms.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.lms.model.OfferedCourse;
import ir.lms.service.OfferedCourseService;
import ir.lms.util.dto.*;
import ir.lms.util.dto.mapper.OfferedCourseMapper;
import ir.lms.util.dto.mapper.ResponseOfferedCourseMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/offeredCourse")
public class OfferedCourseController {

    private final OfferedCourseService offeredCourseService;
    private final ResponseOfferedCourseMapper respMapper;
    private final OfferedCourseMapper mapper;

    public OfferedCourseController(OfferedCourseService offeredCourseService,
                                   OfferedCourseMapper mapper, ResponseOfferedCourseMapper respMapper) {
        this.offeredCourseService = offeredCourseService;
        this.respMapper = respMapper;
        this.mapper = mapper;
    }

    private static final String STUDENT = "hasRole('STUDENT')";
    private static final String TEACHER = "hasRole('STUDENT')";
    private static final String ADMIN_OR_MANAGER = "hasAnyRole('ADMIN','MANAGER')";
    private static final String ALL_AUTHENTICATED = "hasAnyRole('ADMIN','MANAGER','STUDENT')";

    @PreAuthorize(ADMIN_OR_MANAGER)
    @PostMapping
    public ResponseEntity<ApiResponse<ResponseOfferedCourseDTO>> create(@Valid  @RequestBody OfferedCourseDTO dto) {
        OfferedCourse offeredCourse = offeredCourseService.persist(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ResponseOfferedCourseDTO>builder()
                        .success(true)
                        .message("offered.course.creation.success")
                        .data(respMapper.toDto(offeredCourse))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseOfferedCourseDTO>> update(@PathVariable Long id,@Valid @RequestBody OfferedCourseDTO dto) {
        OfferedCourse updated = offeredCourseService.update(id, mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ResponseOfferedCourseDTO>builder()
                        .success(true)
                        .message("offered.course.update.success")
                        .data(respMapper.toDto(updated))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        offeredCourseService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Course deleted success." , true));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResponseOfferedCourseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ResponseOfferedCourseDTO>builder()
                        .success(true)
                        .message("offered.course.get.success")
                        .data(respMapper.toDto(offeredCourseService.findById(id)))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ResponseOfferedCourseDTO>>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<ResponseOfferedCourseDTO>>builder()
                        .success(true)
                        .message("offered.courses.get.success")
                        .data(offeredCourseService.findAll().stream()
                                .map(respMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }


    @PreAuthorize(TEACHER)
    @GetMapping("/teacher-courses")
    public ResponseEntity<ApiResponse<List<ResponseOfferedCourseDTO>>> findAllTeacherCourses(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<ResponseOfferedCourseDTO>>builder()
                        .success(true)
                        .message("offered.courses.get.success")
                        .data(offeredCourseService.findAllTeacherCourse(principal).stream()
                                .map(respMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(STUDENT)
    @GetMapping("/student-courses")
    public ResponseEntity<ApiResponse<List<ResponseOfferedCourseDTO>>> findAllStudentCourses(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<ResponseOfferedCourseDTO>>builder()
                        .success(true)
                        .message("offered.courses.get.success")
                        .data(offeredCourseService.findAllStudentCourses(principal).stream()
                                .map(respMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/term-courses/{termId}")
    public ResponseEntity<ApiResponse<List<ResponseOfferedCourseDTO>>> findAllTermCourses(@PathVariable Long termId, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<ResponseOfferedCourseDTO>>builder()
                        .success(true)
                        .message("courses.get.success")
                        .data(offeredCourseService.findAllTermCourses(termId , principal).stream()
                                .map(respMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

}
