package ir.lms.controller;

import ir.lms.model.ExamTemplate;
import ir.lms.service.ExamService;
import ir.lms.dto.ApiResponseDTO;
import ir.lms.dto.exam.ExamDTO;
import ir.lms.mapper.ExamMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
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

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<ExamDTO> save(@RequestBody ExamDTO dto) {
        ExamTemplate exam = examService.persist(examMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(examMapper.toDto(exam));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/{id}")
    public ResponseEntity<ExamDTO> update(@PathVariable Long id, @RequestBody ExamDTO dto) {
        ExamTemplate foundedExam = examService.findById(id);
        foundedExam.setTitle(dto.getTitle());
        foundedExam.setExamStartTime(dto.getExamStartTime());
        foundedExam.setExamEndTime(dto.getExamEndTime());
        foundedExam.setDescription(dto.getDescription());
        ExamTemplate exam = examService.persist(foundedExam);
        return ResponseEntity.ok(examMapper.toDto(exam));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Exam deleted success." , true));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/{id}")
    public ResponseEntity<ExamDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(examMapper.toDto(examService.findById(id)));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping
    public ResponseEntity<List<ExamDTO>> findAll() {
        List<ExamDTO> examDTOS = new ArrayList<>();
        for (ExamTemplate exam : examService.findAll()) examDTOS.add(examMapper.toDto(exam));
        return ResponseEntity.ok(examDTOS);
    }

    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/course/exams/{courseId}")
    public ResponseEntity<List<ExamDTO>> findAllExamsOfACourse(@PathVariable Long courseId) {
        List<ExamDTO> examDTOS = new ArrayList<>();
        for (ExamTemplate exam : examService.findAllExamOfACourse(courseId)) examDTOS.add(examMapper.toDto(exam));
        return ResponseEntity.ok(examDTOS);
    }

}
