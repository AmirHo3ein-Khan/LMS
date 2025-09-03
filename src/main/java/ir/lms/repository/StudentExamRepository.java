package ir.lms.repository;

import ir.lms.model.ExamInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamRepository extends JpaRepository<ExamInstance,Long> {
}
