package ir.lms.repository;

import ir.lms.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {

    @Query("""
       select q
       from Question q
       join q.examQuestions eq
       where eq.exam.id = :examId
       """)
    List<Question> findQuestionsOfExam(@Param("examId") Long examId);

}
