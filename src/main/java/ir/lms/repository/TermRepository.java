package ir.lms.repository;

import ir.lms.model.Major;
import ir.lms.model.Term;
import ir.lms.model.enums.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term,Long> {
    boolean existsBySemesterAndMajor(Semester semester, Major major);
    List<Term> findByDeletedIsFalse();
}
