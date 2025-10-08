package ir.lms.controller;

import ir.lms.util.dto.AnswerDTO;
import ir.lms.util.dto.ApiResponseDTO;
import ir.lms.util.dto.mapper.AnswerMapper;
import ir.lms.model.Answer;
import ir.lms.model.Option;
import ir.lms.service.AnswerService;
import ir.lms.service.ExamService;
import ir.lms.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private final StudentService studentService;
    private final ExamService examService;
    private final AnswerMapper answerMapper;
    private final AnswerService answerService;

    public StudentController(StudentService studentService,
                             ExamService examService,
                             AnswerMapper answerMapper,
                             AnswerService answerService) {
        this.studentService = studentService;
        this.examService = examService;
        this.answerMapper = answerMapper;
        this.answerService = answerService;
    }


    private static final String STUDENT = "hasRole('STUDENT')";

    @PreAuthorize(STUDENT)
    @PostMapping("/take-course/{courseId}")
    public ResponseEntity<ApiResponseDTO> studentGetCourse(@PathVariable Long courseId, Principal principal) {
        studentService.studentTakeCourse(courseId, principal);
        return ResponseEntity.ok(new ApiResponseDTO("Course get success.", true));
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
}
