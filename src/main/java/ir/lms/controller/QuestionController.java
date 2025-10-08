package ir.lms.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.lms.util.dto.*;
import ir.lms.util.dto.mapper.OptionMapper;
import ir.lms.util.dto.mapper.QuestionMapper;
import ir.lms.model.Option;
import ir.lms.model.Question;
import ir.lms.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionController {
    private final QuestionService questionService;
    private final QuestionMapper questionMapper;
    private final OptionMapper optionMapper;

    public QuestionController(QuestionService questionService, QuestionMapper questionMapper, OptionMapper optionMapper) {
        this.questionService = questionService;
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
    }

    private static final String TEACHER = "hasRole('TEACHER')";
    private static final String TEACHER_OR_STUDENT = "hasAnyRole('TEACHER' , 'STUDENT')";

    @PreAuthorize(TEACHER)
    @PostMapping
    public ResponseEntity<ApiResponse<QuestionDTO>> create(@Valid @RequestBody QuestionDTO dto) {
        List<Option> options = new ArrayList<>();
        if (!(dto.getOptions() == null)) {
            dto.getOptions().stream().map(optionMapper::toEntity).forEach(options::add);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<QuestionDTO>builder()
                        .success(true)
                        .message("question.creation.success")
                        .data(questionMapper.toDto(questionService.createQuestion(dto.getQuestionType(), questionMapper.toEntity(dto), options)))
                                        .timestamp(Instant.now().toString())
                                        .build()
                        );
    }

    @PreAuthorize(TEACHER)
    @PostMapping("/assign-exam")
    public ResponseEntity<ApiResponseDTO> assignQuestionToExam(@Valid @RequestBody ExamQuestionDTO dto) {
        questionService.assignQuestionToExam(dto.getExamId(), dto.getQuestionId(), dto.getScore());
        return ResponseEntity.ok(new ApiResponseDTO("Question added successfully.", true));
    }


    @PreAuthorize(TEACHER_OR_STUDENT)
    @GetMapping("/exam-questions/{examId}")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> findAllQuestionsOfAExam(@PathVariable Long examId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<QuestionDTO>>builder()
                        .success(true)
                        .message("courses.get.success")
                        .data(questionService.findQuestionsByExamId(examId).stream()
                                .map(questionMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }

    @PreAuthorize(TEACHER)
    @GetMapping("/course-questions/{courseId}")
    public ResponseEntity<ApiResponse<List<QuestionDTO>>> findAllQuestionsOfACourse(@PathVariable Long courseId, Principal principal) {
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<QuestionDTO>>builder()
                        .success(true)
                        .message("courses.get.success")
                        .data(questionService.findQuestionsOfCourse(courseId, principal).stream()
                                .map(questionMapper::toDto)
                                .toList())
                        .timestamp(Instant.now().toString())
                        .build()
        );
    }


}
