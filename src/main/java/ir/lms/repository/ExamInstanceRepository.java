package ir.lms.repository;

import ir.lms.model.ExamInstance;
import ir.lms.model.ExamTemplate;
import ir.lms.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamInstanceRepository extends JpaRepository<ExamInstance,Long> {
    Optional<ExamInstance> findByPersonAndExam(Person person, ExamTemplate examTemplate);
}
