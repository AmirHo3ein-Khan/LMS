package ir.lms.repository;

import ir.lms.model.ExamQuestion;
import ir.lms.model.ExamTemplate;
import ir.lms.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion,Long> {
    Optional<ExamQuestion> findByExamAndQuestion(ExamTemplate examTemplate , Question question);
}
