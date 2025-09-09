package ir.lms.controller;

import ir.lms.service.ExamService;
import ir.lms.util.dto.exam.ExamDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exam")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ExamDTO> create(@RequestBody ExamDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examService.create(dto));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ExamDTO> update(@PathVariable Long id , @RequestBody ExamDTO dto) {
        return ResponseEntity.ok(examService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        examService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ExamDTO>> findById() {
        return ResponseEntity.ok(examService.findAll());
    }
}
