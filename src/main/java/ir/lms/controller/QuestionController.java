package ir.lms.controller;

import ir.lms.dto.ApiResponseDTO;
import ir.lms.dto.option.OptionDTO;
import ir.lms.dto.question.ExamQuestionDTO;
import ir.lms.dto.question.QuestionDTO;
import ir.lms.mapper.OptionMapper;
import ir.lms.mapper.QuestionMapper;
import ir.lms.model.Option;
import ir.lms.model.Question;
import ir.lms.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<QuestionDTO> create(@RequestBody QuestionDTO dto) {
        List<Option> options = new ArrayList<>();
        for (OptionDTO optionDTO : dto.getOptions()) {
        options.add(optionMapper.toEntity(optionDTO));
        }
        Question entity = questionMapper.toEntity(dto);
        return ResponseEntity.ok(questionMapper.toDto(questionService.createQuestion(dto.getQuestionType() , entity ,  options)));
    }

    @PostMapping("/assign/exam")
    public ResponseEntity<ApiResponseDTO> assignQuestionToExam(@RequestBody ExamQuestionDTO dto) {
        questionService.assignQuestionToExam(dto.getExamId(), dto.getQuestionId(), dto.getScore());
        return ResponseEntity.ok(new ApiResponseDTO("Question added successfully." , true));
    }


}
