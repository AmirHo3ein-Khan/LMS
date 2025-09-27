package ir.lms.service;

import ir.lms.model.ExamTemplate;
import ir.lms.service.base.BaseService;

import java.security.Principal;
import java.util.List;

public interface ExamService extends BaseService<ExamTemplate, Long> {
    List<ExamTemplate> findAllExamOfACourse(Long courseId);
    void startExam(Long examId , Principal principal);
    void submitExam(Long examId , Principal principal);

}
