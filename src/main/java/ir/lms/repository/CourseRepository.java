package ir.lms.repository;

import ir.lms.model.Course;
import ir.lms.model.Major;
import jakarta.websocket.server.PathParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long> {


    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Course c " +
            "WHERE c.title = :title AND c.major.id = :majorId")
    boolean existsByMajorAndTitle(@Param("majorId") Long majorId, @Param("title") String title);

    Optional<Course> findByMajor(Major major);
}
