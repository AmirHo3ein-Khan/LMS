package ir.lms.repository;

import ir.lms.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MajorRepository extends JpaRepository<Major,Long> {
    boolean existsByMajorName(String majorName);
    Optional<Major> findByMajorName(String majorName);
}
