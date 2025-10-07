package ir.lms.controller;

import ir.lms.dto.ApiResponseDTO;
import ir.lms.dto.exam.ExamDTO;
import ir.lms.dto.option.OptionDTO;
import ir.lms.dto.question.ExamQuestionDTO;
import ir.lms.dto.question.QuestionDTO;
import ir.lms.mapper.OptionMapper;
import ir.lms.mapper.QuestionMapper;
import ir.lms.model.ExamTemplate;
import ir.lms.model.Option;
import ir.lms.model.Question;
import ir.lms.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping
    public ResponseEntity<QuestionDTO> create(@Valid  @RequestBody QuestionDTO dto) {
        List<Option> options = new ArrayList<>();
        if (!(dto.getOptions() == null)) {
            for (OptionDTO optionDTO : dto.getOptions()) {
                options.add(optionMapper.toEntity(optionDTO));
            }
        }
        Question entity = questionMapper.toEntity(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionMapper.toDto(questionService.createQuestion(dto.getQuestionType(), entity, options)));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/assign/exam")
    public ResponseEntity<ApiResponseDTO> assignQuestionToExam(@Valid @RequestBody ExamQuestionDTO dto) {
        questionService.assignQuestionToExam(dto.getExamId(), dto.getQuestionId(), dto.getScore());
        return ResponseEntity.ok(new ApiResponseDTO("Question added successfully.", true));
    }


    @PreAuthorize("hasRole('TEACHER') or hasRole('STUDENT')")
    @GetMapping("/exam/questions/{examId}")
    public ResponseEntity<List<QuestionDTO>> findAllQuestionsOfAExam(@PathVariable Long examId) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questionService.findQuestionsByExamId(examId)) questionDTOList.add(questionMapper.toDto(question));
        return ResponseEntity.ok(questionDTOList);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/course/questions/{courseId}")
    public ResponseEntity<List<QuestionDTO>> findAllQuestionsOfACourse(@PathVariable Long courseId , Principal principal) {
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : questionService.findQuestionsOfCourse(courseId , principal)) questionDTOList.add(questionMapper.toDto(question));
        return ResponseEntity.ok(questionDTOList);
    }


}
