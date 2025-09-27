package ir.lms.repository;

import ir.lms.model.Answer;
import ir.lms.model.ExamInstance;
import ir.lms.model.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer,Long> {
    Optional<Answer> findByExamQuestionAndExamInstance(ExamQuestion examQuestion, ExamInstance examInstance);
}
