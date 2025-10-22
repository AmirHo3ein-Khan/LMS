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
    public ResponseEntity<ExamDTO> save(@Valid @RequestBody ExamDTO dto) {
        ExamTemplate exam = examService.persist(examMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(examMapper.toDto(exam));
    }

    @PreAuthorize(TEACHER)
    @PutMapping("/{id}")
    public ResponseEntity<ExamDTO> update(@PathVariable Long id,@Valid @RequestBody ExamDTO dto) {
        ExamTemplate updated = examService.update(id, examMapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.OK).body(examMapper.toDto(updated));
    }

    @PreAuthorize(TEACHER)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(TEACHER)
    @GetMapping("/{id}")
    public ResponseEntity<ExamDTO> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(examMapper.toDto(examService.findById(id)));
    }

    @PreAuthorize(TEACHER)
    @GetMapping
    public ResponseEntity<List<ExamDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(
                examService.findAll().stream()
                                .map(examMapper::toDto)
                                .toList());
    }

    @PreAuthorize(TEACHER_OR_STUDENT)
    @GetMapping("/course-exams/{courseId}")
    public ResponseEntity<List<ExamDTO>> findAllExamsOfACourse(@PathVariable Long courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                examService.findAllExamOfACourse(courseId).stream()
                                .map(examMapper::toDto)
                                .toList());
    }

    @PreAuthorize(STUDENT)
    @PostMapping("/start-exam/{examId}")
    public ResponseEntity<Void> studentStartExam(@PathVariable Long examId, Principal principal) {
        examService.startExam(examId, principal);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(STUDENT)
    @PostMapping("/submit-exam/{examId}")
    public ResponseEntity<Void> studentSubmitExam(@PathVariable Long examId, Principal principal) {
        examService.submitExam(examId, principal);
        return ResponseEntity.ok().build();
    }


    @PreAuthorize(STUDENT)
    @PostMapping("/submit-answer")
    public ResponseEntity<Void> submitAnswer(@Valid  @RequestBody AnswerDTO answerDTO , Principal principal) {
        Answer answer = answerMapper.toEntity(answerDTO , principal);
        Option option = new Option();
        if (answerDTO.getOptionId() != null) {
            option = answerService.findOptionById(answerDTO.getOptionId());
        }
        answerService.saveAnswer(answerDTO.getType() , answer, option, answerDTO.getAnswerText());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize(TEACHER)
    @PostMapping("/grading-descriptive")
    public ResponseEntity<Void> gradingDescriptiveQuestionOfExam(@RequestBody GradingDTO dto) {
        gradingService.descriptiveGrading(dto.getExamId(), dto.getStudentId() , dto.getQuestionId() ,  dto.getScore());
        return ResponseEntity.ok().build();
    }

}
