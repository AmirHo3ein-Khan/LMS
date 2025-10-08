package ir.lms.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.lms.model.ExamTemplate;
import ir.lms.service.ExamService;
import ir.lms.util.dto.ApiResponse;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.ExamDTO;
import ir.lms.util.dto.mapper.ExamMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamService examService;
    private final ExamMapper examMapper;

    public ExamController(ExamService examService, ExamMapper examMapper) {
        this.examService = examService;
        this.examMapper = examMapper;
    }

    private static final String TEACHER = "hasRole('TEACHER')";
    private static final String TEACHER_OR_STUDENT = "hasRole('TEACHER') or hasRole('STUDENT')";

    @PreAuthorize(TEACHER)
    @PostMapping
    public ResponseEntity<ApiResponse<ExamDTO>> save(@Valid @RequestBody ExamDTO dto) {
        ExamTemplate exam = examService.persist(examMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<ExamDTO>builder()
                        .success(true)
                        .message("exam.creation.success")
                        .data(examMapper.toDto(exam))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(TEACHER)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamDTO>> update(@PathVariable Long id,@Valid @RequestBody ExamDTO dto) {
        ExamTemplate updated = examService.update(id, examMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ExamDTO>builder()
                        .success(true)
                        .message("exam.update.success")
                        .data(examMapper.toDto(updated))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(TEACHER)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Exam deleted success." , true));
    }

    @PreAuthorize(TEACHER)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<ExamDTO>builder()
                        .success(true)
                        .message("exam.get.success")
                        .data(examMapper.toDto(examService.findById(id)))
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(TEACHER)
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExamDTO>>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<ExamDTO>>builder()
                        .success(true)
                        .message("exams.get.success")
                        .data(examService.findAll().stream()
                                .map(examMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(TEACHER_OR_STUDENT)
    @GetMapping("/course-exams/{courseId}")
    public ResponseEntity<ApiResponse<List<ExamDTO>>> findAllExamsOfACourse(@PathVariable Long courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<ExamDTO>>builder()
                        .success(true)
                        .message("exams.get.success")
                        .data(examService.findAllExamOfACourse(courseId).stream()
                                .map(examMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

}
