package ir.lms.service;

import ir.lms.model.ExamTemplate;
import ir.lms.model.Major;
import ir.lms.service.base.BaseService;

import java.util.List;

public interface ExamService extends BaseService<ExamTemplate, Long> {
    List<ExamTemplate> findAllExamOfACourse(Long courseId);
}
