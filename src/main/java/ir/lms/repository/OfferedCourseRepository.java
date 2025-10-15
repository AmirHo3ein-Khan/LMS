package ir.lms.repository;

import ir.lms.model.OfferedCourse;
import ir.lms.model.Person;
import ir.lms.model.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Repository
public interface OfferedCourseRepository extends JpaRepository<OfferedCourse,Long> {
    @Query("SELECT SUM(oc.course.unit) " +
            "FROM OfferedCourse oc JOIN oc.students s " +
            "WHERE s.id = :studentId")
    Integer getTotalUnitsByPersonId(@Param("studentId") Long studentId);



    @Query("""
    select (count(oc) > 0)
    from OfferedCourse oc
    where oc.teacher = :teacher AND
         oc.term = :term AND
         oc.dayOfWeek = :dayOfWeek AND
         oc.classStartTime < :classEndTime AND
         oc.classEndTime > :classStartTime
    """)
    boolean existsOverlappingCourse(@Param("teacher") Person teacher,
                                    @Param("term") Term term,
                                    @Param("classStartTime") LocalTime classStartTime,
                                    @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                    @Param("classEndTime") LocalTime classEndTime);

}