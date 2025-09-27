package ir.lms.service;

public interface GradingService {
    void autoTestGrading(Long examId, Long studentId);
    void descriptiveGrading(Long examId, Long studentId, Long questionId , Double score);
}
