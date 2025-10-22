package ir.lms.controller;

import ir.lms.model.Answer;
import ir.lms.model.ExamTemplate;
import ir.lms.model.Option;
import ir.lms.service.AnswerService;
import ir.lms.service.ExamService;
import ir.lms.service.GradingService;
import ir.lms.util.dto.*;
import ir.lms.util.dto.mapper.AnswerMapper;
import ir.lms.util.dto.mapper.ExamMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamService examService;
    private final ExamMapper examMapper;
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;
    private final GradingService gradingService;

    public ExamController(ExamService examService, ExamMapper examMapper, AnswerService answerService, AnswerMapper answerMapper, GradingService gradingService) {
        this.examService = examService;
        this.examMapper = examMapper;
        this.answerService = answerService;
        this.answerMapper = answerMapper;
        this.gradingService = gradingService;
    }

    private static final String TEACHER = "hasRole('TEACHER')";
    private static final String STUDENT = "hasRole('STUDENT')";
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

    @PreAuthorize(STUDENT)
    @PostMapping("/start-exam/{examId}")
    public ResponseEntity<ApiResponseDTO> studentStartExam(@PathVariable Long examId, Principal principal) {
        examService.startExam(examId, principal);
        return ResponseEntity.ok(new ApiResponseDTO("Exam start success.", true));
    }

    @PreAuthorize(STUDENT)
    @PostMapping("/submit-exam/{examId}")
    public ResponseEntity<ApiResponseDTO> studentSubmitExam(@PathVariable Long examId, Principal principal) {
        examService.submitExam(examId, principal);
        return ResponseEntity.ok(new ApiResponseDTO("Exam submit success.", true));
    }


    @PreAuthorize(STUDENT)
    @PostMapping("/submit-answer")
    public ResponseEntity<ApiResponseDTO> submitAnswer(@Valid  @RequestBody AnswerDTO answerDTO , Principal principal) {
        Answer answer = answerMapper.toEntity(answerDTO , principal);
        Option option = new Option();
        if (answerDTO.getOptionId() != null) {
            option = answerService.findOptionById(answerDTO.getOptionId());
        }
        answerService.saveAnswer(answerDTO.getType() , answer, option, answerDTO.getAnswerText());
        return ResponseEntity.ok(new ApiResponseDTO("Answer saved success.", true));
    }

    @PreAuthorize(TEACHER)
    @PostMapping("/grading-descriptive")
    public ResponseEntity<ApiResponseDTO> gradingDescriptiveQuestionOfExam(@RequestBody GradingDTO dto) {
        gradingService.descriptiveGrading(dto.getExamId(), dto.getStudentId() , dto.getQuestionId() ,  dto.getScore());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponseDTO("Grading success", true));
    }

}
