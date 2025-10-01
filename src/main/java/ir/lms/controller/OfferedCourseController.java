package ir.lms.controller;

import ir.lms.model.Major;
import ir.lms.model.OfferedCourse;
import ir.lms.model.enums.CourseStatus;
import ir.lms.service.OfferedCourseService;
import ir.lms.dto.ApiResponseDTO;
import ir.lms.dto.offeredCourse.OfferedCourseDTO;
import ir.lms.dto.offeredCourse.ResponseOfferedCourseDTO;
import ir.lms.mapper.OfferedCourseMapper;
import ir.lms.mapper.ResponseOfferedCourseMapper;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/offeredCourse")
public class OfferedCourseController {

    private final OfferedCourseService offeredCourseService;
    private final OfferedCourseMapper mapper;
    private final ResponseOfferedCourseMapper respMapper;

    public OfferedCourseController(OfferedCourseService offeredCourseService, OfferedCourseMapper mapper, ResponseOfferedCourseMapper respMapper) {
        this.offeredCourseService = offeredCourseService;
        this.mapper = mapper;
        this.respMapper = respMapper;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseOfferedCourseDTO> create(@RequestBody OfferedCourseDTO dto) {
        OfferedCourse offeredCourse = offeredCourseService.persist(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(respMapper.toDto(offeredCourse));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseOfferedCourseDTO> update(@PathVariable Long id, @RequestBody OfferedCourseDTO dto) {
        OfferedCourse foundedCourse = offeredCourseService.findById(id);
        foundedCourse.setEndTime(dto.getEndTime());
        foundedCourse.setCapacity(dto.getCapacity());
        foundedCourse.setClassLocation(dto.getClassLocation());
        foundedCourse.setStartTime(dto.getStartTime());
        OfferedCourse offeredCourse = offeredCourseService.persist(foundedCourse);
        return ResponseEntity.ok(respMapper.toDto(offeredCourse));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        offeredCourseService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Course deleted success." , true));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseOfferedCourseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(respMapper.toDto(offeredCourseService.findById(id)));
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAll() {
        List<ResponseOfferedCourseDTO> courseDTOS = new ArrayList<>();
        for (OfferedCourse course : offeredCourseService.findAll()) courseDTOS.add(respMapper.toDto(course));
        return ResponseEntity.ok(courseDTOS);
    }


    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/teacher/courses")
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAllTeacherCourses(Principal principal) {
        List<ResponseOfferedCourseDTO> courseDTOS = new ArrayList<>();
        for (OfferedCourse course : offeredCourseService.findAllTeacherCourse(principal)) courseDTOS.add(respMapper.toDto(course));
        return ResponseEntity.ok(courseDTOS);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/courses")
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAllStudentCourses(Principal principal) {
        List<OfferedCourse> studentCourses = offeredCourseService.findAllStudentCourses(principal);
        List<ResponseOfferedCourseDTO> courses = new ArrayList<>();
        for (OfferedCourse course : studentCourses) {
            ResponseOfferedCourseDTO dto = respMapper.toDto(course);
            courses.add(dto);
        }
        return ResponseEntity.ok(courses);
    }

    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('MANAGER')")
    @GetMapping("/term/courses/{termId}")
    public ResponseEntity<List<ResponseOfferedCourseDTO>> findAllTermCourses(@PathVariable Long termId, Principal principal) {
        List<OfferedCourse> termCourses = offeredCourseService.findAllTermCourses(termId , principal);
        List<ResponseOfferedCourseDTO> courses = new ArrayList<>();
        for (OfferedCourse course : termCourses) {
            ResponseOfferedCourseDTO dto = respMapper.toDto(course);
            courses.add(dto);
        }
        return ResponseEntity.ok(courses);
    }

}
