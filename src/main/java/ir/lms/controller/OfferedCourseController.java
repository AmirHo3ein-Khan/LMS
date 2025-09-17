package ir.lms.controller;

import ir.lms.model.Course;
import ir.lms.model.OfferedCourse;
import ir.lms.service.OfferedCourseService;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.course.CourseDTO;
import ir.lms.util.dto.offeredCourse.OfferedCourseDTO;
import ir.lms.util.mapper.OfferedCourseMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/offeredCourse")
public class OfferedCourseController {

    private final OfferedCourseService offeredCourseService;
    private final OfferedCourseMapper mapper;

    public OfferedCourseController(OfferedCourseService offeredCourseService, OfferedCourseMapper mapper) {
        this.offeredCourseService = offeredCourseService;
        this.mapper = mapper;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<OfferedCourseDTO> createOfferedCourse(@RequestBody OfferedCourseDTO dto) {
        OfferedCourse offeredCourse = offeredCourseService.persist(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(offeredCourse));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OfferedCourseDTO> update(@PathVariable Long id, @RequestBody OfferedCourseDTO dto) {
        OfferedCourse offeredCourse = offeredCourseService.persist(mapper.toEntity(dto));
        offeredCourse.setId(id);
        return ResponseEntity.ok(mapper.toDto(offeredCourse));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        offeredCourseService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Course deleted success." , true));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OfferedCourseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toDto(offeredCourseService.findById(id)));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<OfferedCourseDTO>> findAll() {
        List<OfferedCourseDTO> courseDTOS = new ArrayList<>();
        for (OfferedCourse course : offeredCourseService.findAll()) courseDTOS.add(mapper.toDto(course));
        return ResponseEntity.ok(courseDTOS);
    }
}
