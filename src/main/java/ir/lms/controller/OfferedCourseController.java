package ir.lms.controller;

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
    public ResponseEntity<ResponseOfferedCourseDTO> create(@Valid  @RequestBody OfferedCourseDTO dto) {
        OfferedCourse offeredCourse = offeredCourseService.persist(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(respMapper.toDto(offeredCourse));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @PutMapping("/{id}")
    public ResponseEntity<ResponseOfferedCourseDTO> update(@PathVariable Long id,@Valid @RequestBody OfferedCourseDTO dto) {
        OfferedCourse updated = offeredCourseService.update(id, mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(respMapper.toDto(updated));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        offeredCourseService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping("/{id}")
    public ResponseEntity<ResponseOfferedCourseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(respMapper.toDto(offeredCourseService.findById(id)));
    }

    @PreAuthorize(ADMIN_OR_MANAGER)
    @GetMapping
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(offeredCourseService.findAll().stream()
                                .map(respMapper::toDto)
                                .toList());
    }


    @PreAuthorize(TEACHER)
    @GetMapping("/teacher-courses")
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAllTeacherCourses(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(offeredCourseService.findAllTeacherCourse(principal).stream()
                                .map(respMapper::toDto)
                                .toList());
    }

    @PreAuthorize(STUDENT)
    @GetMapping("/student-courses")
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAllStudentCourses(Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(offeredCourseService.findAllStudentCourses(principal).stream()
                                .map(respMapper::toDto)
                                .toList());
    }

    @PreAuthorize(ALL_AUTHENTICATED)
    @GetMapping("/term-courses/{termId}")
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAllTermCourses(@PathVariable Long termId, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(offeredCourseService.findAllTermCourses(termId , principal).stream()
                                .map(respMapper::toDto)
                                .toList());
    }

}
