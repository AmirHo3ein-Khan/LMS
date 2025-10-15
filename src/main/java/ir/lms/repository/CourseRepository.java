package ir.lms.repository;

import ir.lms.model.Course;
import ir.lms.model.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {
    Optional<Course> findByTitleAndMajor(String title, Major major);
}
